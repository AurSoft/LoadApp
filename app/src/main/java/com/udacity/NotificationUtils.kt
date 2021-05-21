package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

// Notification ID.
private val NOTIFICATION_ID = 0

fun NotificationManager.sendDownloadNotification(messageBody: String, applicationContext: Context, extras: Map<String, String>) {
    val detailsIntent =
            Intent(applicationContext, DetailActivity::class.java)
                .apply {
                    extras.map {
                        putExtra(it.key, it.value)
                    }
                }
    val detailsPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            detailsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.notification_channel))
            .setSmallIcon(R.drawable.baseline_downloading_24)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(messageBody)
            .addAction(
                    R.drawable.baseline_downloading_24,
                    applicationContext.getString(R.string.notification_button),
                    detailsPendingIntent
            )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.createChannel(channelId: String, channelName: String, channelDesc: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setShowBadge(false)
        }

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)
        notificationChannel.description = channelDesc

        createNotificationChannel(notificationChannel)
    }
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}