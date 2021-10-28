package com.khedr.runtracking.di

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.khedr.runtracking.R
import com.khedr.runtracking.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.khedr.runtracking.utils.Constants.NOTIFICATION_TITLE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context,
    ) = FusedLocationProviderClient(context)

    @ServiceScoped
    @Provides
    fun provideRunFragmentPendingIntent(@ApplicationContext context: Context): PendingIntent =
        NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_host)
            .setDestination(R.id.runFragment)
            .createPendingIntent()

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent,
    ): NotificationCompat.Builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_runner_light)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)


}
