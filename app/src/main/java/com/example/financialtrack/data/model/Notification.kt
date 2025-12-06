package com.example.financialtrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val navigationType: String = "none",  // Target page to navigate: "transactions", "debts", "profile", "reports", "none"
    val createdAt: Long = System.currentTimeMillis()
)

enum class NotificationType {
    BUDGET_ALERT, DEBT_REMINDER, TRANSACTION_ALERT, GENERAL
}
