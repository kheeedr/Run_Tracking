package com.khedr.runtracking.presentation.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.khedr.runtracking.utils.Constants.ACTION_PAUSE_SERVICE
import com.khedr.runtracking.utils.Constants.ACTION_RESUME_SERVICE
import com.khedr.runtracking.utils.Constants.ACTION_START_SERVICE
import com.khedr.runtracking.utils.Constants.ACTION_STOP_SERVICE
import com.khedr.runtracking.utils.Constants.FASTEST_LOCATION_INTERVAL
import com.khedr.runtracking.utils.Constants.LOCATION_UPDATE_INTERVAL
import com.khedr.runtracking.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.khedr.runtracking.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.khedr.runtracking.utils.Constants.NOTIFICATION_ID
import com.khedr.runtracking.utils.Constants.TIMER_UPDATE_INTERVAL
import com.khedr.runtracking.utils.ServiceState
import com.khedr.runtracking.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder
    private lateinit var curNotificationBuilder: NotificationCompat.Builder

    private val timeInSeconds = MutableLiveData(0L)

    companion object {
        val timeInMillis = MutableLiveData(0L)
        val pathPoints = MutableLiveData<Polylines>(mutableListOf())
        val serviceState = MutableLiveData(ServiceState.STOPPED)
    }

    /// ------------------------------------------------   on create service
    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder = baseNotificationBuilder
        serviceState.observe(this, {
            updateLocationTracking(it)
            updateNotificationState(it)
        })
    }

    //  ------------------------------------------------  on start command service
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_SERVICE -> startForegroundService()
                ACTION_PAUSE_SERVICE -> pauseService()
                ACTION_RESUME_SERVICE -> resumeService()
                ACTION_STOP_SERVICE -> killService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // -------------------------------------------------------------------------   start service
    private fun startForegroundService() {
        serviceState.postValue(ServiceState.STARTED)

        addEmptyPolyLine()
        startTimer()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeInSeconds.observe(this, {
            if (serviceState.value != ServiceState.STOPPED) {
                val notification = curNotificationBuilder
                    .setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        })
    }

    // -------------------------------------------------------------------------   pause service
    private fun pauseService() {
        serviceState.postValue(ServiceState.PAUSED)
        stopTimer()
    }

    // --------------------------------------------------------------------------  resume service
    private fun resumeService() {
        serviceState.postValue(ServiceState.STARTED)
        addEmptyPolyLine()
        startTimer()
    }

    // ---------------------------------------------------------------------------  kill service
    private fun killService() {
        serviceState.postValue(ServiceState.STOPPED)
        pathPoints.postValue(mutableListOf())
        timeInSeconds.postValue(0L)
        timeInMillis.postValue(0L)
        stopTimer()
        stopForeground(true)
        stopSelf()
    }

    // -------------------------------------------------------------------------- timer management

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var totalRunningTime = 0L
    private var startTime = 0L
    private var completedSecondsInMillis = 0L

    private fun stopTimer() {
        isTimerEnabled = false
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (serviceState.value == ServiceState.STARTED) {

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

    // -------------------------------------------------------------------------------  polyline
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
    // ------------------------------------------------------------------------------ location

    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (serviceState.value == ServiceState.STARTED)
                result.locations.let { locations ->
                    for (location in locations) addPathPoints(location)
                }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(serviceState: ServiceState) {
        if (serviceState == ServiceState.STARTED) {
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

    // -------------------------------------------------------------------------  notification
    private fun updateNotificationState(serviceState: ServiceState) {

        val notificationActionText =
            if (serviceState == ServiceState.STARTED) "Pause" else "Resume"

        val actionIntent = Intent(this, TrackingService::class.java).apply {
            action =
                if (serviceState == ServiceState.STARTED)
                    ACTION_PAUSE_SERVICE
                else
                    ACTION_START_SERVICE
        }
        val pendingIntent =
            PendingIntent.getService(this,
                if (serviceState == ServiceState.STARTED) 1 else 2,
                actionIntent,
                FLAG_UPDATE_CURRENT
            )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        if (serviceState != ServiceState.STOPPED) {
            curNotificationBuilder = baseNotificationBuilder
                .addAction(
                    if (serviceState == ServiceState.STARTED) android.R.drawable.ic_media_play
                    else android.R.drawable.ic_media_pause,
                    notificationActionText,
                    pendingIntent
                )
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }

    }

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