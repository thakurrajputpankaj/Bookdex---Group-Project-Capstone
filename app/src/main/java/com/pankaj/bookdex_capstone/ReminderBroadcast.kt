package com.pankaj.bookdex_capstone

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class ReminderBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
        createNotificationChannel(notificationManager)

        val notificationBuilder = NotificationCompat.Builder(context, "remindToReadChannel")
            .setSmallIcon(R.drawable.search_icon) // Set your own icon
            .setContentTitle("Read Books Reminder")
            .setContentText("Remind to Read the books")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(200, notificationBuilder.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Read Books Reminder Channel"
            val descriptionText = "Channel for Book Read Reminder"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("remindToReadChannel", name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
