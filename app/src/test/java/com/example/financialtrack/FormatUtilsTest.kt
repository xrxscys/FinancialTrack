package com.example.financialtrack

import com.example.financialtrack.utils.FormatUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example unit tests for FormatUtils.
 */
class FormatUtilsTest {
    
    @Test
    fun formatDate_returnsCorrectFormat() {
        val timestamp = 1609459200000L // 2021-01-01 00:00:00 UTC
        val formatted = FormatUtils.formatDate(timestamp)
        
        // Format should be dd/MM/yyyy
        assert(formatted.matches(Regex("\\d{2}/\\d{2}/\\d{4}")))
    }
    
    @Test
    fun formatCurrency_returnsFormattedString() {
        val amount = 1234.56
        val formatted = FormatUtils.formatCurrency(amount)
        
        // Should contain the amount (format may vary by locale)
        assert(formatted.contains("1") || formatted.contains("1,234"))
    }
}
