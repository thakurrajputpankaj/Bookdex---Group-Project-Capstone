package com.pankaj.bookdex_capstone

import android.app.*
import android.content.*
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.core.app.NotificationCompat
import java.util.*

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        notificationSwitch = findViewById(R.id.switch_notification)
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                scheduleNotification()
            } else {
                cancelNotification()
            }
        }
    }


    private fun scheduleNotification() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val timeAtButtonClick = System.currentTimeMillis()
        val tenSecondsInMillis = 100L

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            timeAtButtonClick,
            tenSecondsInMillis,
            pendingIntent
        )
    }

    private fun cancelNotification() {
        val intent = Intent(this, ReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}
