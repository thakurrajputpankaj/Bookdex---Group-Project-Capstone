package com.pankaj.bookdex_capstone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import java.util.*

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_TRIGGER_NOTIFICATION") {
            buildNotification(context)
        } else {
            buildNotification(context)
        }
    }

    private fun buildNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(context, NotificationActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, "book_reminder")
            .setSmallIcon(R.drawable.alarm)
            .setContentTitle("Reminder")
            .setContentText("Do not forget to read your books !")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        private const val NOTIFICATION_ID = 123
    }
}
