package com.example.financialtrack.utils

object Constants {
    // SharedPreferences
    const val PREF_NAME = "FinancialTrackPrefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    // Transaction Categories
    val INCOME_CATEGORIES = listOf(
        "Salary",
        "Business",
        "Investment",
        "Gift",
        "Other Income"
    )
    
    val EXPENSE_CATEGORIES = listOf(
        "Food & Dining",
        "Transportation",
        "Shopping",
        "Entertainment",
        "Bills & Utilities",
        "Healthcare",
        "Education",
        "Other Expense"
    )
    
    // Date Formats
    const val DATE_FORMAT = "dd/MM/yyyy"
    const val DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm"
    
    // Notification Channels
    const val CHANNEL_ID_BUDGET_ALERT = "budget_alert"
    const val CHANNEL_ID_DEBT_REMINDER = "debt_reminder"
    const val CHANNEL_ID_GENERAL = "general"
}
