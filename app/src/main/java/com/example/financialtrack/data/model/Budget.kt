package com.example.financialtrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val category: String,
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: Long,
    val endDate: Long
)

enum class BudgetPeriod {
    DAILY, WEEKLY, MONTHLY, YEARLY
}
