package com.example.financialtrack.utils

import android.content.Context
import android.util.Log
import com.example.financialtrack.data.model.Notification
import com.example.financialtrack.data.model.NotificationType
import com.google.firebase.auth.FirebaseAuth

/**
 * NotificationService is a template service for generating and managing in-app notifications.
 * This is designed to be easily customizable by the team for specific notification types.
 */
class NotificationService(private val context: Context) {

    private val notificationManager = FinancialTrackNotificationManager(context)
    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    /**
     * Template method for creating notifications
     * Easily extensible for new notification types
     */
    fun createNotification(
        title: String,
        message: String,
        type: NotificationType = NotificationType.GENERAL,
        navigationType: FinancialTrackNotificationManager.NavigationType = FinancialTrackNotificationManager.NavigationType.NONE,
        showSystemNotification: Boolean = true,
        debtId: Long? = null  // Optional debt ID for deduplication
    ): Notification? {
        val userId = currentUserId ?: return null

        return Notification(
            userId = userId,
            title = title,
            message = message,
            type = type,
            isRead = false,
            navigationType = navigationType.destination,
            debtId = debtId  // Store debt ID for deduplication
        ).also {
            if (showSystemNotification) {
                notificationManager.showNotification(title, message, navigationType)
            }
            Log.d("NotificationService", "Notification created: $title")
        }
    }

    /**
     * Template notification type: Bill Reminder
     * 
     * @param billName Name of the bill/debt
     * @param dueDate Formatted due date string
     * @param debtId Unique debt ID for deduplication in database
     */
    fun createBillReminderNotification(
        billName: String,
        dueDate: String,
        debtId: Long? = null
    ): Notification? {
        return createNotification(
            title = "Bill Reminder",
            message = "Your $billName is due on $dueDate",
            type = NotificationType.DEBT_REMINDER,
            navigationType = FinancialTrackNotificationManager.NavigationType.DEBTS,
            debtId = debtId  // Pass debt ID for deduplication
        )
    }

    /**
     * Template notification type: Large Transaction Alert
     */
    fun createLargeTransactionNotification(
        amount: Double,
        description: String
    ): Notification? {
        return createNotification(
            title = "Unusual Transaction",
            message = "Large transaction detected: $$amount - $description",
            type = NotificationType.TRANSACTION_ALERT,
            navigationType = FinancialTrackNotificationManager.NavigationType.TRANSACTIONS
        )
    }

    /**
     * Template notification type: Budget Alert
     */
    fun createBudgetAlertNotification(
        category: String,
        percentageUsed: Int
    ): Notification? {
        return createNotification(
            title = "Budget Alert",
            message = "$category budget is $percentageUsed% utilized",
            type = NotificationType.BUDGET_ALERT,
            navigationType = FinancialTrackNotificationManager.NavigationType.NONE
        )
    }

    /**
     * Template notification type: Financial Goal Update
     */
    fun createGoalUpdateNotification(
        goalName: String,
        progress: Int
    ): Notification? {
        return createNotification(
            title = "Goal Progress",
            message = "$goalName is now $progress% complete",
            type = NotificationType.GENERAL,
            navigationType = FinancialTrackNotificationManager.NavigationType.PROFILE
        )
    }

    /**
     * Template for custom/generic notifications
     * Override this for specific notification types
     */
    fun createGenericNotification(
        title: String,
        message: String
    ): Notification? {
        return createNotification(
            title = title,
            message = message,
            type = NotificationType.GENERAL,
            navigationType = FinancialTrackNotificationManager.NavigationType.NONE
        )
    }

    /**
     * Notification when a new loan is added
     */
    fun createLoanAddedNotification(
        loanName: String,
        amount: Double,
        dueDate: String,
        debtId: Long? = null
    ): Notification? {
        return createNotification(
            title = "Loan Added",
            message = "New loan '$loanName' for ₱${String.format("%.2f", amount)} due on $dueDate",
            type = NotificationType.DEBT_REMINDER,
            navigationType = FinancialTrackNotificationManager.NavigationType.DEBTS,
            debtId = debtId
        )
    }

    /**
     * Notification when a loan is deleted
     */
    fun createLoanDeletedNotification(
        loanName: String,
        amount: Double,
        debtId: Long? = null
    ): Notification? {
        return createNotification(
            title = "Loan Deleted",
            message = "Loan '$loanName' for ₱${String.format("%.2f", amount)} has been removed",
            type = NotificationType.DEBT_REMINDER,
            navigationType = FinancialTrackNotificationManager.NavigationType.DEBTS,
            debtId = debtId
        )
    }
}
