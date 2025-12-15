package com.example.financialtrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val creditorName: String,
    val amount: Double,
    val amountPaid: Double = 0.0,
    val dueDate: Long,
    val interestRate: Double = 0.0,
    val type: DebtType,
    val description: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val paidAt: Long? = null,
    val lastNotificationTime: Long? = null,
    // Notification flags - track which notification ranges have fired
    val notified5Days: Boolean = false,
    val notified3Days: Boolean = false,
    val notified1Day: Boolean = false,
    val notified5Hours: Boolean = false,
    val notified3Hours: Boolean = false,
    val notified1Hour: Boolean = false,
    val notifiedOverdue: Boolean = false
)

enum class DebtType {
    LOAN, DEBT, CREDIT_CARD
}
