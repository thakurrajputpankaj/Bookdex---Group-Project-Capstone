package com.pankaj.bookdex_capstone

import android.app.*
import android.content.*
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)

        notificationSwitch = findViewById(R.id.switch_notification)
        notificationSwitch.isChecked = sharedPreferences.getBoolean("switchState", false)

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                scheduleNotification()
            } else {
                cancelNotification()
            }
            sharedPreferences.edit().putBoolean("switchState", isChecked).apply()
        }
    }

    private fun scheduleNotification() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val timeAtButtonClick = System.currentTimeMillis()
        val tenSecondsInMillis = 10000L

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            timeAtButtonClick,
            tenSecondsInMillis,
            pendingIntent
        )
    }

    private fun cancelNotification() {
        val intent = Intent(this, ReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
}
