package com.example.financialtrack.service

import android.content.Context
import com.example.financialtrack.data.model.Debt
import com.example.financialtrack.data.repository.DebtRepository
import com.example.financialtrack.data.repository.NotificationRepository
import com.example.financialtrack.utils.NotificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Centralized loan notification manager.
 * 
 * Uses the existing NotificationService template for consistency with app-wide notification handling.
 * 
 * Range-based evaluation (no exact time matches):
 * - 5 days: > 3 days and <= 5 days
 * - 3 days: > 1 day and <= 3 days  
 * - 1 day: > 5 hours and <= 1 day
 * - 5 hours: > 3 hours and <= 5 hours
 * - 3 hours: > 1 hour and <= 3 hours
 * - 1 hour: > 0 and <= 1 hour
 * - Overdue: <= 0
 */
class LoanNotificationManager(
    private val context: Context,
    private val notificationRepository: NotificationRepository,
    private val debtRepository: DebtRepository? = null
) {

    private val notificationService = NotificationService(context)

    /**
     * CENTRALIZED CHECK - Called from:
     * - DebtActivity.onCreate()
     * - DebtActivity.onResume()
     * - After loan creation/update
     * - Periodically via WorkManager
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
     * Evaluate a single loan and trigger notification if in range and not already notified.
     */
    private suspend fun evaluateAndNotify(debt: Debt, currentTime: Long) {
        return withContext(Dispatchers.IO) {
            // Skip inactive loans
            if (!debt.isActive) {
                return@withContext
            }

            val timeRemaining = debt.dueDate - currentTime

            when {
                // OVERDUE: <= 0 ms (past deadline)
                timeRemaining <= 0 -> {
                    if (!debt.notifiedOverdue) {
                        sendNotification(
                            debt,
                            "Overdue: Your loan '${debt.creditorName}' is already due."
                        )
                        debtRepository?.updateNotificationFlag(debt.id, "notifiedOverdue", true)
                    }
                }

                // 1 HOUR: 0 < time <= 1 hour
                timeRemaining <= TimeUnit.HOURS.toMillis(1) -> {
                    if (!debt.notified1Hour) {
                        sendNotification(
                            debt,
                            "Reminder: Your loan '${debt.creditorName}' is due in 1 hour."
                        )
                        debtRepository?.updateNotificationFlag(debt.id, "notified1Hour", true)
                    }
                }

                // 3 HOURS: 1 hour < time <= 3 hours
                timeRemaining <= TimeUnit.HOURS.toMillis(3) -> {
                    if (!debt.notified3Hours) {
                        sendNotification(
                            debt,
                            "Reminder: Your loan '${debt.creditorName}' is due in 3 hours."
                        )
                        debtRepository?.updateNotificationFlag(debt.id, "notified3Hours", true)
                    }
                }

                // 5 HOURS: 3 hours < time <= 5 hours
                timeRemaining <= TimeUnit.HOURS.toMillis(5) -> {
                    if (!debt.notified5Hours) {
                        sendNotification(
                            debt,
                            "Reminder: Your loan '${debt.creditorName}' is due in 5 hours."
                        )
                        debtRepository?.updateNotificationFlag(debt.id, "notified5Hours", true)
                    }
                }

                // 1 DAY: 5 hours < time <= 1 day
                timeRemaining <= TimeUnit.DAYS.toMillis(1) -> {
                    if (!debt.notified1Day) {
                        sendNotification(
                            debt,
                            "Reminder: Your loan '${debt.creditorName}' is due in 1 day."
                        )
                        debtRepository?.updateNotificationFlag(debt.id, "notified1Day", true)
                    }
                }

                // 3 DAYS: 1 day < time <= 3 days
                timeRemaining <= TimeUnit.DAYS.toMillis(3) -> {
                    if (!debt.notified3Days) {
                        sendNotification(
                            debt,
                            "Reminder: Your loan '${debt.creditorName}' is due in 3 days."
                        )
                        debtRepository?.updateNotificationFlag(debt.id, "notified3Days", true)
                    }
                }

                // 5 DAYS: 3 days < time <= 5 days
                timeRemaining <= TimeUnit.DAYS.toMillis(5) -> {
                    if (!debt.notified5Days) {
                        sendNotification(
                            debt,
                            "Reminder: Your loan '${debt.creditorName}' is due in 5 days."
                        )
                        debtRepository?.updateNotificationFlag(debt.id, "notified5Days", true)
                    }
                }
            }
        }
    }

    /**
     * Send notification using the existing NotificationService template.
     * This ensures consistency with all other app notifications.
     */
    private suspend fun sendNotification(debt: Debt, message: String) {
        return withContext(Dispatchers.IO) {
            try {
                notificationService.createBillReminderNotification(
                    billName = debt.creditorName,
                    dueDate = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.US)
                        .format(java.util.Date(debt.dueDate))
                )
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
