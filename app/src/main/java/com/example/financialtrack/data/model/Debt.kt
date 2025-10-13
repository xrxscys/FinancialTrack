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
    val description: String = ""
)

enum class DebtType {
    LOAN, DEBT, CREDIT_CARD
}
