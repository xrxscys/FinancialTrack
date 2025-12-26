package com.example.financialtrack.data.repository

import androidx.lifecycle.LiveData
import com.example.financialtrack.data.database.DebtDao
import com.example.financialtrack.data.model.Debt

class DebtRepository(private val debtDao: DebtDao) {
    
    fun getAllDebts(userId: String): LiveData<List<Debt>> {
        return debtDao.getAllDebts(userId)
    }
    
    suspend fun getActiveDebts(userId: String): List<Debt> {
        return debtDao.getActiveDebts(userId)
    }
    
    suspend fun getPaidDebts(userId: String): List<Debt> {
        return debtDao.getPaidDebts(userId)
    }
    
    suspend fun getDebtById(id: Long): Debt? {
        return debtDao.getDebtById(id)
    }
    
    suspend fun insert(debt: Debt): Long {
        return debtDao.insert(debt)
    }
    
    suspend fun update(debt: Debt) {
        debtDao.update(debt)
    }
    
    suspend fun delete(debt: Debt) {
        debtDao.delete(debt)
    }
    
    suspend fun updateNotificationFlag(debtId: Long, flagName: String, value: Boolean) {
        val debt = getDebtById(debtId) ?: return
        val updatedDebt = when (flagName) {
            "notified5Days" -> debt.copy(notified5Days = value)
            "notified3Days" -> debt.copy(notified3Days = value)
            "notified1Day" -> debt.copy(notified1Day = value)
            "notified5Hours" -> debt.copy(notified5Hours = value)
            "notified3Hours" -> debt.copy(notified3Hours = value)
            "notified1Hour" -> debt.copy(notified1Hour = value)
            "notifiedOverdue" -> debt.copy(notifiedOverdue = value)
            else -> debt
        }
        update(updatedDebt)
    }
    
    suspend fun clearAllNotificationFlags(debtId: Long) {
        val debt = getDebtById(debtId) ?: return
        val clearedDebt = debt.copy(
            notified5Days = false,
            notified3Days = false,
            notified1Day = false,
            notified5Hours = false,
            notified3Hours = false,
            notified1Hour = false,
            notifiedOverdue = false
        )
        update(clearedDebt)
    }

    suspend fun deductLoanPayment(debtId: Long, paymentAmount: Double) {
        val debt = getDebtById(debtId) ?: return
        val newAmountPaid = debt.amountPaid + paymentAmount
        val newRemainingBalance = (debt.amount - newAmountPaid).coerceAtLeast(0.0)
        
        val updatedDebt = debt.copy(
            amountPaid = newAmountPaid,
            remainingBalance = newRemainingBalance
        )
        
        update(updatedDebt)
        
        // Auto-complete if balance reaches zero
        if (newRemainingBalance == 0.0) {
            completeLoanAutomatically(debtId)
        }
    }

    private suspend fun completeLoanAutomatically(debtId: Long) {
        val debt = getDebtById(debtId) ?: return
        
        if (debt.isActive && debt.remainingBalance == 0.0) {
            val completedDebt = debt.copy(
                isActive = false,
                paidAt = System.currentTimeMillis()
            )
            update(completedDebt)
        }
    }
}
