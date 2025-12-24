package com.example.financialtrack.service

import android.content.Context
import androidx.work.*
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.repository.DebtRepository
import com.example.financialtrack.data.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Worker that runs periodically to check loan deadlines and trigger notifications.
 * Uses periodic time evaluation to determine if notifications should be sent.
 */
class DebtReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val database = AppDatabase.getDatabase(applicationContext)
                val debtRepository = DebtRepository(database.debtDao())
                val notificationRepository = NotificationRepository(database.notificationDao())
                val notificationManager = LoanNotificationManager(
                    applicationContext,
                    notificationRepository,
                    debtRepository
                )
                
                // Check all active loans for notifications
                notificationManager.checkLoanNotifications()
                
                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.retry()
            }
        }
    }

    companion object {
        private const val DEBT_REMINDER_WORK_NAME = "debt_reminder_periodic_work"

        /**
         * Schedule the debt reminder worker to run periodically in background.
         * MANDATORY: Ensures notifications fire even when app is closed.
         * 
         * Runs every 15 minutes (Android minimum for reliable scheduling).
         * Worker loads active loans and calls checkLoanNotifications() via LoanNotificationManager.
         */
        fun scheduleDebtReminders(context: Context) {
            val debtReminderWork = PeriodicWorkRequestBuilder<DebtReminderWorker>(
                15, // Repeat every 15 minutes (Android minimum)
                TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                DEBT_REMINDER_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                debtReminderWork
            )
        }

        /**
         * Cancel the debt reminder worker.
         * Called when user signs out or app is uninstalled.
         */
        fun cancelDebtReminders(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(DEBT_REMINDER_WORK_NAME)
        }
    }
}
