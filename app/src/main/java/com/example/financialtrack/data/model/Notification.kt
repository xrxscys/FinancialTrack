package com.example.financialtrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["userId"], name = "idx_notifications_userId"),
        Index(value = ["userId", "debtId"], unique = false, name = "idx_notifications_user_debt")
    ]
)
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val navigationType: String = "none",  // Target page: "transactions", "debts", "profile", "reports", "none"
    val createdAt: Long = System.currentTimeMillis(),
    val debtId: Long? = null  // Reference to Debt ID for deduplication (null for non-debt notifications)
)

enum class NotificationType {
    BUDGET_ALERT, DEBT_REMINDER, TRANSACTION_ALERT, GENERAL
}
