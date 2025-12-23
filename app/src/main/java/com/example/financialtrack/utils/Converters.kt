package com.example.financialtrack.utils

import androidx.room.TypeConverter
import com.example.financialtrack.data.model.*

class Converters {
    
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }
    
    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
    
    @TypeConverter
    fun fromBudgetPeriod(value: BudgetPeriod): String {
        return value.name
    }
    
    @TypeConverter
    fun toBudgetPeriod(value: String): BudgetPeriod {
        return BudgetPeriod.valueOf(value)
    }
    
    @TypeConverter
    fun fromDebtType(value: DebtType): String {
        return value.name
    }
    
    @TypeConverter
    fun toDebtType(value: String): DebtType {
        return DebtType.valueOf(value)
    }
    
    @TypeConverter
    fun fromNotificationType(value: NotificationType): String {
        return value.name
    }
    
    @TypeConverter
    fun toNotificationType(value: String): NotificationType {
        return NotificationType.valueOf(value)
    }
}
