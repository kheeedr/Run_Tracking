package com.khedr.runtracking.presentation.ui.main_screens

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.khedr.runtracking.R
import com.khedr.runtracking.databinding.FragmentRunBinding
import com.khedr.runtracking.presentation.MainViewModel
import com.khedr.runtracking.presentation.services.Polyline
import com.khedr.runtracking.presentation.services.TrackingService
import com.khedr.runtracking.utils.Constants.ACTION_PAUSE_SERVICE
import com.khedr.runtracking.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.khedr.runtracking.utils.Constants.MAP_ZOOM
import com.khedr.runtracking.utils.Constants.POLYLINE_COLOR
import com.khedr.runtracking.utils.Constants.POLYLINE_WIDTH
import com.khedr.runtracking.utils.TrackingUtility


class RunFragment : Fragment() {
    lateinit var b: FragmentRunBinding
    private var map: GoogleMap? = null
    private lateinit var viewModel: MainViewModel

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var curTimeInMillis = 0L
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.mapView.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        b.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }

        b.btRunPauseResume.setOnClickListener {
            sendCommandToService(
                if (!isTracking) ACTION_START_OR_RESUME_SERVICE
                else ACTION_PAUSE_SERVICE
            )
        }

        subscribeToObserves()
    }


    private fun subscribeToObserves() {
        TrackingService.isTracking.observe(viewLifecycleOwner, {
            isTracking = it
            updateUI(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, {
            pathPoints = it
            addLatestPolyLine()
            moveCameraToLatestPolyLine()
        })

        TrackingService.timeInMillis.observe(viewLifecycleOwner, {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis)
            b.tvTimer.text = formattedTime
        })

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
        }
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

    private fun moveCameraToLatestPolyLine() {

        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun updateUI(isTracking: Boolean) {
        if (isTracking) {
            b.btRunPauseResume.text = "Pause Running"
        } else {
            b.btRunPauseResume.text = "Resume Running"
        }
    }

    override fun onResume() {
        super.onResume()
        b.mapView.onResume()
        addAllPolylines()
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_run, container, false)
        return b.root
    }

    override fun onStart() {
        super.onStart()
        b.mapView.onResume()
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