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
 * DebugNotificationHelper provides utilities for testing notifications
 * in debug/development mode without needing to background the app.
 * 
 * Use this to:
 * - Test system notifications while app is open
 * - Show visual banners + system notifications simultaneously
 * - Verify notification appearance during development
 */
class DebugNotificationHelper(private val context: Context) {

    companion object {
        private const val DEBUG_CHANNEL_ID = "financial_track_debug"
        private const val DEBUG_CHANNEL_NAME = "Debug Notifications"
        private const val DEBUG_CHANNEL_DESCRIPTION = "Test notifications that show even when app is open"
        private var debugNotificationId = 2001
    }

    init {
        createDebugChannel()
    }

    /**
     * Create a special debug channel with HIGH importance to show notifications
     * even when app is in foreground
     */
    private fun createDebugChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH  // HIGH to bypass foreground suppression
            val channel = NotificationChannel(DEBUG_CHANNEL_ID, DEBUG_CHANNEL_NAME, importance).apply {
                description = DEBUG_CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
                setSound(
                    android.provider.Settings.System.DEFAULT_NOTIFICATION_URI,
                    android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Show a system notification that bypasses foreground suppression (HIGH priority)
     * Perfect for testing without backgrounding the app
     * 
     * @param title Notification title
     * @param message Notification message  
     * @param navigationType Where to navigate on click
     */
    fun showDebugSystemNotification(
        title: String,
        message: String,
        navigationType: FinancialTrackNotificationManager.NavigationType = FinancialTrackNotificationManager.NavigationType.NONE
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

        val notification = NotificationCompat.Builder(context, DEBUG_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // HIGH priority
            .setCategory(NotificationCompat.CATEGORY_ALARM)  // Allows showing in foreground
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            .setVibrate(longArrayOf(0, 500, 250, 500))  // Vibration pattern
            .build()

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(debugNotificationId++, notification)
    }

    /**
     * Show a persistent debug banner at top of screen (using notification style)
     * Shows with HIGH priority and vibration
     * 
     * @param title Banner title
     * @param message Banner message
     */
    fun showDebugBanner(
        title: String,
        message: String
    ) {
        // Create a system notification that acts as a banner
        val notification = NotificationCompat.Builder(context, DEBUG_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🧪 DEBUG: $title")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVibrate(longArrayOf(0, 100, 50, 100))  // Quick vibration
            .setShowWhen(true)
            .build()

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(debugNotificationId++, notification)
    }

    /**
     * Clear all debug notifications
     */
    fun clearAllDebugNotifications() {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}
