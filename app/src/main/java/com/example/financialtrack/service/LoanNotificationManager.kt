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
     *
     * PREVENTS DUPLICATES BY:
     * 1. Using notified1Day flag to prevent multiple checks
     * 2. Passing debtId to notification for database-level deduplication
     * 3. Repository checks if notification already exists for this debt
     * 4. Only creates new notification if doesn't exist
     * 5. Logging duplicate prevention for debugging
     */
    private suspend fun sendNotification(debt: Debt) {
        return withContext(Dispatchers.IO) {
            try {
                val dateFormat = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.US)
                val dueDate = dateFormat.format(java.util.Date(debt.dueDate))

                // Create notification object with debtId for deduplication
                val notification = notificationService.createBillReminderNotification(
                    billName = debt.creditorName,
                    dueDate = dueDate,
                    debtId = debt.id  // Pass debtId for duplicate prevention
                )

                // Save notification to database for persistence in notification page
                // Repository handles deduplication - returns -1 if duplicate prevented
                if (notification != null) {
                    val result = notificationRepository.insert(notification)
                    if (result != -1L) {
                        android.util.Log.d(
                            "LoanNotificationManager",
                            "✓ Notification inserted for debt ${debt.id} (${debt.creditorName})"
                        )
                    } else {
                        android.util.Log.d(
                            "LoanNotificationManager",
                            "⊘ Duplicate notification prevented for debt ${debt.id} (${debt.creditorName})"
                        )
                    }
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

    /**
     * Send notification when a new loan is added.
     * Prevents duplicates using database deduplication.
     */
    suspend fun sendLoanAddedNotification(debt: Debt) {
        return withContext(Dispatchers.IO) {
            try {
                val dateFormat = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.US)
                val dueDate = dateFormat.format(java.util.Date(debt.dueDate))

                // Create notification object with debtId for deduplication
                val notification = notificationService.createLoanAddedNotification(
                    loanName = debt.creditorName,
                    amount = debt.amount,
                    dueDate = dueDate,
                    debtId = debt.id
                )

                // Save notification to database - repository handles deduplication
                if (notification != null) {
                    val result = notificationRepository.insert(notification)
                    if (result != -1L) {
                        android.util.Log.d(
                            "LoanNotificationManager",
                            "✓ Loan added notification for ${debt.creditorName}"
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Send notification when a loan is deleted.
     * Prevents duplicates using database deduplication.
     */
    suspend fun sendLoanDeletedNotification(debt: Debt) {
        return withContext(Dispatchers.IO) {
            try {
                val notification = notificationService.createLoanDeletedNotification(
                    loanName = debt.creditorName,
                    amount = debt.amount,
                    debtId = debt.id
                )

                // Save notification to database - repository handles deduplication
                if (notification != null) {
                    val result = notificationRepository.insert(notification)
                    if (result != -1L) {
                        android.util.Log.d(
                            "LoanNotificationManager",
                            "✓ Loan deleted notification for ${debt.creditorName}"
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
