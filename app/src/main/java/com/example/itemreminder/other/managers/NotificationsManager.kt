package com.example.itemreminder.other.managers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.itemreminder.R
import com.example.itemreminder.view.activities.RegisterActivity

object NotificationsManager {

    private const val CHANNEL_ID = "CHANNEL_ID"

    private fun createNotificationChanel(context: Context) {
        val name = "Notification Chanel"
        val description = "Notification Chanel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        channel.description = description
        notificationManager.createNotificationChannel(channel)
    }

    fun display(context: Context) {
        createNotificationChanel(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Item Reminder")
            .setSmallIcon(R.drawable.cart)
            .setContentText("New Item Added")
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(2, builder.build())
    }

    fun serviceNotification(context: Context): Notification {
        val sendIntent = PendingIntent.getActivity(context, 0, Intent(context, RegisterActivity::class.java), PendingIntent.FLAG_MUTABLE)
        createNotificationChanel(context)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Service Notification")
            .setSmallIcon(R.drawable.cart)
            .setContentIntent(sendIntent)
            .setContentText("I'm still working in background").build()
    }
}