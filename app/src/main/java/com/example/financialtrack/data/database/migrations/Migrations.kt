package com.example.financialtrack.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add remainingBalance column to debts table
        // Default value: copy from amount column (loan starts with full balance remaining)
        database.execSQL(
            "ALTER TABLE debts ADD COLUMN remainingBalance REAL NOT NULL DEFAULT 0.0"
        )
        
        // Update all existing loans: set remainingBalance = amount - amountPaid
        database.execSQL(
            "UPDATE debts SET remainingBalance = (amount - amountPaid)"
        )
    }
}
