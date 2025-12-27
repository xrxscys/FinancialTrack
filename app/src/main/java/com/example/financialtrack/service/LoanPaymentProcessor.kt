package com.example.financialtrack.service

import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.repository.DebtRepository

class LoanPaymentProcessor(private val database: AppDatabase) {
    
    private val debtRepository = DebtRepository(database.debtDao())
    
    suspend fun processTransactionForLoanPayments(transaction: Transaction, selectedLoanId: Long? = null) {
        // Only process if:
        // 1. Type is EXPENSE
        // 2. Amount > 0
        // 3. Loan is explicitly selected (selectedLoanId != null)
        
        val isExpense = transaction.type.toString() == "EXPENSE"
        val isValidAmount = transaction.amount > 0
        
        if (!isExpense || !isValidAmount || selectedLoanId == null) {
            return
        }
        
        // Apply payment to the specified loan
        val loan = database.debtDao().getDebtById(selectedLoanId)
        if (loan != null && loan.isActive) {
            debtRepository.deductLoanPayment(loan.id, transaction.amount)
        }
    }
}
