package com.example.financialtrack.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object FormatUtils {
    
    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance()
        return format.format(amount)
    }
    
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
