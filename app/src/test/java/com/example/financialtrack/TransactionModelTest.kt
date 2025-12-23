package com.example.financialtrack

import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Example local unit test for Transaction model.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TransactionModelTest {
    
    @Test
    fun transaction_creation_isCorrect() {
        val transaction = Transaction(
            id = 1,
            userId = "user123",
            amount = 100.0,
            type = TransactionType.INCOME,
            category = "Salary",
            description = "Monthly salary",
            date = System.currentTimeMillis()
        )
        
        assertNotNull(transaction)
        assertEquals(1, transaction.id)
        assertEquals("user123", transaction.userId)
        assertEquals(100.0, transaction.amount, 0.01)
        assertEquals(TransactionType.INCOME, transaction.type)
        assertEquals("Salary", transaction.category)
    }
    
    @Test
    fun transaction_types_areCorrect() {
        val income = TransactionType.INCOME
        val expense = TransactionType.EXPENSE
        
        assertEquals("INCOME", income.name)
        assertEquals("EXPENSE", expense.name)
    }
}
