package com.khedr.runtracking.presentation.ui.main_screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.FragmentRunBinding
import com.khedr.runtracking.domain.model.Run
import com.khedr.runtracking.presentation.MainViewModel
import com.khedr.runtracking.presentation.services.Polyline
import com.khedr.runtracking.presentation.services.TrackingService
import com.khedr.runtracking.utils.Constants.ACTION_PAUSE_SERVICE
import com.khedr.runtracking.utils.Constants.ACTION_START_SERVICE
import com.khedr.runtracking.utils.Constants.ACTION_STOP_SERVICE
import com.khedr.runtracking.utils.Constants.MAP_ZOOM
import com.khedr.runtracking.utils.Constants.POLYLINE_COLOR
import com.khedr.runtracking.utils.Constants.POLYLINE_WIDTH
import com.khedr.runtracking.presentation.services.ServiceState
import com.khedr.runtracking.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.round

@AndroidEntryPoint
class RunFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var b: FragmentRunBinding

    private var map: GoogleMap? = null

    private var pathPoints = mutableListOf<Polyline>()

    private var curTimeInMillis = 0L

    private var marker: Marker? = null

    private val weight: Float = 80f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.mapView.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        b.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }

        b.btRunStart.setOnClickListener(this)
        b.btRunPause.setOnClickListener(this)
        b.btRunResume.setOnClickListener(this)
        b.btRunFinish.setOnClickListener(this)
        b.btRunCancel.setOnClickListener(this)

        subscribeToServiceObserves()
    }

    private fun subscribeToServiceObserves() {
        TrackingService.serviceState.observe(viewLifecycleOwner, { updateUI(it) })

        TrackingService.pathPoints.observe(viewLifecycleOwner, {
            pathPoints = it
            addLatestPolyLine()
            moveCameraToLastPoint()
        })

        TrackingService.timeInMillis.observe(viewLifecycleOwner, {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis)
            setTimerText(formattedTime)
            setTimerProgress(curTimeInMillis)
        })
    }

    private fun setTimerProgress(curTimeInMillis: Long) {

        val needRunningTimeInSeconds = (15 * 60).toFloat()
        val curTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(curTimeInMillis).toFloat()
        val progressPercent = ceil((curTimeInSeconds / needRunningTimeInSeconds) * 100)
        b.progressbarTimer.progress = progressPercent.toInt()

    }

    private fun setTimerText(formattedTime: String) {
        b.tvTimer.apply {
            text = formattedTime
        }
    }

    private fun addLatestPolyLine() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastPoint = pathPoints.last()[pathPoints.last().size - 2]
            val lastPoint = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastPoint)
                .add(lastPoint)
            map?.addPolyline(polylineOptions)

            moveMarkerWhenLocationChanged(Location(LocationManager.GPS_PROVIDER).apply {
                latitude = lastPoint.latitude
                longitude = lastPoint.longitude
            })
        }
    }

    private fun moveMarkerWhenLocationChanged(location: Location?) {

        if (location == null) return

        if (marker == null) {
            marker = map?.addMarker(
                MarkerOptions()
                    .flat(true)
                    .icon(
                        TrackingUtility.bitmapDescriptorFromVector(
                            requireContext(),
                            R.drawable.ic_location_static
                        )
                    )
                    .anchor(0.5f, 0.5f)
                    .position(LatLng(location.latitude, location.longitude))
            )
        }
        marker?.let { animateMarker(it, location) } // Helper method for smooth
    }

    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun moveCameraToLastPoint() {

        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }


    private fun updateUI(serviceState: ServiceState) {

        when (serviceState) {
            ServiceState.STOPPED -> {
                b.btRunStart.visibility = View.VISIBLE
                b.btRunPause.visibility = View.GONE
                b.btRunFinish.visibility = View.GONE
                b.btRunResume.visibility = View.GONE
                b.btRunCancel.visibility = View.GONE
                b.lottieRunner.pauseAnimation()
                b.tvTimer.text = "00 : 00"
            }
            ServiceState.STARTED -> {
                b.btRunStart.visibility = View.GONE
                b.btRunPause.visibility = View.VISIBLE
                b.btRunFinish.visibility = View.GONE
                b.btRunResume.visibility = View.GONE
                b.btRunCancel.visibility = View.VISIBLE
                b.lottieRunner.playAnimation()

            }
            ServiceState.PAUSED -> {
                b.btRunStart.visibility = View.GONE
                b.btRunPause.visibility = View.GONE
                b.btRunFinish.visibility = View.VISIBLE
                b.btRunResume.visibility = View.VISIBLE
                b.btRunCancel.visibility = View.VISIBLE
                b.lottieRunner.pauseAnimation()
            }
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onClick(v: View) {
        when (v) {
            b.btRunStart -> sendCommandToService(ACTION_START_SERVICE)
            b.btRunResume -> sendCommandToService(ACTION_START_SERVICE)
            b.btRunPause -> sendCommandToService(ACTION_PAUSE_SERVICE)
            b.btRunCancel -> showCancelTrackingDialog()
            b.btRunFinish -> finishRun()
        }
    }


    private fun showCancelTrackingDialog() {
        val activity = requireActivity()
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure to cancel the current run and delete all its data?")
            .setIcon(R.drawable.ic_cancel)
            .setPositiveButton("Yes") { _, _ ->

                sendCommandToService(ACTION_STOP_SERVICE)
                findNavController().navigateUp()
                val snackBar = Snackbar.make(
                    activity.findViewById(R.id.mainActivityParent),
                    "Run canceled successfully",
                    Snackbar.LENGTH_LONG
                )
                snackBar.setAnchorView(R.id.fab_main_page)
                snackBar.show()

            }.setNegativeButton("No", null)
            .create()

        dialog.show()
    }

    private fun zoomToSeeWholeRun() {
        val bounds = LatLngBounds.builder()
        if (pathPoints.size > 1) {
            for (polyline in pathPoints) {
                for (position in polyline) bounds.include(position)
            }
            map?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds.build(),
                    b.mapView.width,
                    b.mapView.height,
                    (b.mapView.width * 0.05f).toInt()
                )
            )
        }
    }

    private fun finishRun() {
        zoomToSeeWholeRun()
        saveRunToDatabase()
    }

    private fun saveRunToDatabase() {
        val activity = requireActivity()
        map?.snapshot { bitmap ->
            if (bitmap != null) {
                var distanceInMeters = 0
                if (pathPoints.size > 1)
                    for (polyline in pathPoints)
                        distanceInMeters += TrackingUtility.calculatePolylineLength(polyline)
                            .toInt()
                val avgSpeed =
                    round((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
                val dateTimeStamp = Calendar.getInstance().timeInMillis
                val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()

                val run = Run(
                    bitmap,
                    dateTimeStamp,
                    avgSpeed,
                    distanceInMeters,
                    curTimeInMillis,
                    caloriesBurned
                )

                viewModel.saveRunToDatabase(run)

                val snackBar = Snackbar.make(
                    activity.findViewById(R.id.mainActivityParent),
                    "Run saved successfully",
                    Snackbar.LENGTH_LONG
                )
                snackBar.setAnchorView(R.id.fab_main_page)
                snackBar.show()
            } else {

                val snackBar = Snackbar.make(
                    activity.findViewById(R.id.mainActivityParent),
                    "Sorry, Run cant be saved ",
                    Snackbar.LENGTH_LONG
                )
                snackBar.setAnchorView(R.id.fab_main_page)
                snackBar.show()
            }
        }
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigateUp()
    }

    override fun onResume() {
        super.onResume()
        b.mapView.onResume()
        addAllPolylines()
        updateUI(TrackingService.serviceState.value ?: ServiceState.STOPPED)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_run, container, false)
        return b.root
    }

    private fun animateMarker(marker: Marker, location: Location) {
        val handler = Handler(Looper.getMainLooper())
        val start = SystemClock.uptimeMillis()
        val startLatLng: LatLng = marker.position
        val startRotation: Float = marker.rotation
        val duration: Long = 500
        val interpolator: Interpolator = LinearInterpolator()
        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t: Float = interpolator.getInterpolation(elapsed.toFloat() / duration)
                val lng = t * location.longitude + (1 - t) * startLatLng.longitude
                val lat = t * location.latitude + (1 - t) * startLatLng.latitude
                val rotation = (t * location.bearing + (1 - t) * startRotation)
                marker.position = LatLng(lat, lng)
                marker.rotation = rotation
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        b.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        b.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        b.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        b.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        b.mapView.onSaveInstanceState(outState)
    }


}