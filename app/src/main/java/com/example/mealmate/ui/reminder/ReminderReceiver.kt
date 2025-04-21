// ReminderReceiver.kt
package com.example.mealmate.ui.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mealmate.MealMateApplication
import com.example.mealmate.R
import com.example.mealmate.ui.MainActivity
import com.example.mealmate.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra(Constants.EXTRA_REMINDER_ID, -1)
        val title = intent.getStringExtra("title") ?: "MealMate Reminder"
        val message = intent.getStringExtra("message") ?: "It's time to prepare your meal!"

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for meal preparation"
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open app when notification is tapped
        val contentIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Show notification
        with(NotificationManagerCompat.from(context)) {
            notify(reminderId, notification)
        }

        // Mark reminder as completed
        if (reminderId != -1) {
            val application = context.applicationContext as MealMateApplication
            val reminderRepository = application.reminderRepository

            CoroutineScope(Dispatchers.IO).launch {
                val reminder = reminderRepository.getReminderById(reminderId)
                reminder?.let {
                    val updatedReminder = it.copy(isCompleted = true)
                    reminderRepository.update(updatedReminder)
                }
            }
        }
    }
}