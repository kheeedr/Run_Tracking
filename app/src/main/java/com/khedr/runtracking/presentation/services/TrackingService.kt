package com.khedr.runtracking.presentation.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDeepLinkBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.khedr.runtracking.R
import com.khedr.runtracking.utils.Constants.ACTION_PAUSE_SERVICE
import com.khedr.runtracking.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.khedr.runtracking.utils.Constants.ACTION_STOP_SERVICE
import com.khedr.runtracking.utils.Constants.APP_NAME
import com.khedr.runtracking.utils.Constants.FASTEST_LOCATION_INTERVAL
import com.khedr.runtracking.utils.Constants.LOCATION_UPDATE_INTERVAL
import com.khedr.runtracking.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.khedr.runtracking.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.khedr.runtracking.utils.Constants.NOTIFICATION_ID
import com.khedr.runtracking.utils.Constants.TIMER_UPDATE_INTERVAL
import com.khedr.runtracking.utils.TrackingUtility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var isFirstRun = true
    private val timeInSeconds = MutableLiveData<Long>()


    companion object {
        val timeInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
    }

    private fun postInitialValue() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeInSeconds.postValue(0L)
        timeInMillis.postValue(0L)
    }

    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        postInitialValue()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, {
            updateLocationTracking(it)
        })

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {

            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        startTimer()
                    }

                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {

                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled = false
    }


    private fun addEmptyPolyLine() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun addPathPoints(location: Location?) {
        location?.let {
            val position = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(position)
                pathPoints.postValue(this)
            }
        }
    }

    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!) result.locations.let { locations ->
                for (location in locations) addPathPoints(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallBack,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
        }
    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var totalRunningTime = 0L
    private var startTime = 0L
    private var completedSecondsInMillis = 0L

    private fun startTimer() {

        addEmptyPolyLine()
        isTracking.postValue(true)

        startTime = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {

                lapTime = System.currentTimeMillis() - startTime

                timeInMillis.postValue(totalRunningTime + lapTime)
                //check to change seconds in the notification
                if (timeInMillis.value!! >= completedSecondsInMillis + 1000) {
                    // seconds++
                    timeInSeconds.postValue(timeInSeconds.value!! + 1)
                    completedSecondsInMillis += 1000
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            totalRunningTime += lapTime
        }
    }

    private fun startForegroundService() {

        startTimer()
        isTracking.postValue(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_runner_light)
                .setContentTitle(APP_NAME)
                .setContentText("00:00:00")
                .setContentIntent(getRunFragmentPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getRunFragmentPendingIntent() =
        NavDeepLinkBuilder(this)
            .setGraph(R.navigation.nav_host)
            .setDestination(R.id.runFragment)
            .createPendingIntent()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}