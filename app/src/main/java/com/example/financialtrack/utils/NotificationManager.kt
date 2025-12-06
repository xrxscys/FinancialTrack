package com.example.financialtrack.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.financialtrack.MainActivity
import com.example.financialtrack.R

/**
 * NotificationManager handles all system notifications (push notifications)
 * displayed in the device's notification panel
 */
class FinancialTrackNotificationManager(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "financial_track_notifications"
        private const val CHANNEL_NAME = "Financial Track Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for financial tracking alerts and reminders"
        private var notificationId = 1001
    }

    init {
        createNotificationChannel()
    }

    /**
     * Create notification channel for Android 8.0 and above
     * Uses HIGH importance to show notifications even when app is open
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val audioAttributes = android.media.AudioAttributes.Builder()
                .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                setSound(
                    android.provider.Settings.System.DEFAULT_NOTIFICATION_URI,
                    audioAttributes
                )
                enableLights(true)
                lightColor = android.graphics.Color.BLUE
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Show a system notification in the device's notification panel
     * @param title The notification title
     * @param message The notification message
     * @param navigationType The page to navigate to when clicked
     */
    fun showNotification(
        title: String,
        message: String,
        navigationType: NavigationType = NavigationType.NONE
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("navigate_to", navigationType.destination)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setLights(android.graphics.Color.BLUE, 1000, 1000)
            .build()

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId++, notification)
    }

    /**
     * Enum for different navigation types when clicking a notification
     */
    enum class NavigationType(val destination: String) {
        TRANSACTIONS("transactions"),
        DEBTS("debts"),
        PROFILE("profile"),
        REPORTS("reports"),
        NONE("none")
    }
}
