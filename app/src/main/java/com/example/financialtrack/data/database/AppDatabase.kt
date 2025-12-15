package com.example.financialtrack.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.financialtrack.data.model.*
import com.example.financialtrack.data.database.*
import com.example.financialtrack.utils.Converters
import com.example.financialtrack.data.database.dao.FinancialGoalDao
import com.example.financialtrack.data.model.FinancialGoal

@TypeConverters(Converters::class)
@Database(
    entities = [
        User::class,
        Transaction::class,
        Budget::class,
        Debt::class,
        Notification::class,
        FinancialGoal::class
    ],
    version = 6,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun debtDao(): DebtDao
    abstract fun notificationDao(): NotificationDao
    abstract fun financialGoalDao(): FinancialGoalDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "financial_track_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromGoalStatus(status: GoalStatus): String = status.name

    @TypeConverter
    fun toGoalStatus(status: String): GoalStatus = GoalStatus.valueOf(status)
}
