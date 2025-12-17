package com.example.financialtrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val amount: Double,
    val accountId: Int,
    val type: TransactionType,
    val category: String,
    val description: String,
    val date: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}
