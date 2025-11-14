package com.example.financialtrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class GoalStatus {
    ACTIVE,
    COMPLETED,
    ARCHIVED
}

@Entity(tableName = "financial_goals")
data class FinancialGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: String,
    val goalName: String,
    val targetAmount: Double,

    val savedAmount: Double = 0.0,
    val createdDate: Long = System.currentTimeMillis(),
    val deadline: Long,
    val status: GoalStatus = GoalStatus.ACTIVE
)
