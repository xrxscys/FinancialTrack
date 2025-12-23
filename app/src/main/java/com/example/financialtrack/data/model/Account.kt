package com.example.financialtrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class AccountType {
    BANK, CASH, WALLET, OTHER
}

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val name: String,
    val type: AccountType,
    val balance: Double = 0.0
)

class AccountTypeConverter {
    @TypeConverter
    fun fromAccountType(type: AccountType): String = type.name

    @TypeConverter
    fun toAccountType(type: String): AccountType = AccountType.valueOf(type)
}