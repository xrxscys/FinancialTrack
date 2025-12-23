package com.example.financialtrack.service

import android.content.Context
import com.example.financialtrack.data.model.Debt
import com.example.financialtrack.data.repository.DebtRepository
import com.example.financialtrack.data.repository.NotificationRepository
import com.example.financialtrack.utils.NotificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Centralized loan notification manager.
 * 
 * Sends notifications at 10:00 AM on the due date.
 * One notification per loan, never duplicates.
 * Persists across app restarts via WorkManager.
 */
class LoanNotificationManager(
    private val context: Context,
    private val notificationRepository: NotificationRepository,
    private val debtRepository: DebtRepository? = null
) {

    private val notificationService = NotificationService(context)

    /**
     * CENTRALIZED CHECK - Called from:
     * - DebtReminderWorker (every 15 minutes)
     * - DebtActivity.onCreate()
     * - DebtActivity.onResume()
     * - After loan creation/update
     */
    suspend fun checkLoanNotifications(currentTime: Long = System.currentTimeMillis()) {
        return withContext(Dispatchers.IO) {
            try {
                if (debtRepository != null) {
                    val activeDebts = debtRepository.getActiveDebts("user123")
                    for (debt in activeDebts) {
                        evaluateAndNotify(debt, currentTime)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Evaluate a single loan and trigger notification if today is the due date at 10 AM
     * and notification hasn't been sent yet.
     */
    private suspend fun evaluateAndNotify(debt: Debt, currentTime: Long) {
        return withContext(Dispatchers.IO) {
            // Skip inactive loans
            if (!debt.isActive) {
                return@withContext
            }

            // Skip if notification already sent
            if (debt.notified1Day) {
                return@withContext
            }

            // Get current time and due date in calendar format
            val currentCalendar = Calendar.getInstance()
            currentCalendar.timeInMillis = currentTime

            val dueCalendar = Calendar.getInstance()
            dueCalendar.timeInMillis = debt.dueDate

            // Check if today (by day/month/year) is the due date
            val isToday = (
                currentCalendar.get(Calendar.YEAR) == dueCalendar.get(Calendar.YEAR) &&
                currentCalendar.get(Calendar.MONTH) == dueCalendar.get(Calendar.MONTH) &&
                currentCalendar.get(Calendar.DAY_OF_MONTH) == dueCalendar.get(Calendar.DAY_OF_MONTH)
            )

            // Check if current time is at or past 10:00 AM
            val isAtOrPast10AM = (
                currentCalendar.get(Calendar.HOUR_OF_DAY) >= 10
            )

            // Send notification only if today is due date and it's 10 AM or later
            if (isToday && isAtOrPast10AM) {
                sendNotification(debt)
                debtRepository?.updateNotificationFlag(debt.id, "notified1Day", true)
            }
        }
    }

    /**
     * Send notification using NotificationService and persist to database.
     */
    private suspend fun sendNotification(debt: Debt) {
        return withContext(Dispatchers.IO) {
            try {
                val dateFormat = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.US)
                val dueDate = dateFormat.format(java.util.Date(debt.dueDate))
                
                // Create and get the notification object
                val notification = notificationService.createBillReminderNotification(
                    billName = debt.creditorName,
                    dueDate = dueDate
                )
                
                // Save notification to database for persistence in notification page
                if (notification != null) {
                    notificationRepository.insert(notification)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Clear all notification flags when loan is marked as paid.
     * MANDATORY: Called from DebtActivity.markDebtAsPaid()
     */
    suspend fun clearNotificationsForPaidDebt(debt: Debt) {
        return withContext(Dispatchers.IO) {
            if (debtRepository != null) {
                debtRepository.clearAllNotificationFlags(debt.id)
            }
        }
    }
}
