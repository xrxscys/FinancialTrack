package com.example.financialtrack

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.financialtrack.utils.Constants

class FinancialTrackApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val budgetChannel = NotificationChannel(
                Constants.CHANNEL_ID_BUDGET_ALERT,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for budget alerts"
            }
            
            val debtChannel = NotificationChannel(
                Constants.CHANNEL_ID_DEBT_REMINDER,
                "Debt Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for debt payments"
            }
            
            val generalChannel = NotificationChannel(
                Constants.CHANNEL_ID_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "General notifications"
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(budgetChannel)
            notificationManager.createNotificationChannel(debtChannel)
            notificationManager.createNotificationChannel(generalChannel)
        }
    }
}
