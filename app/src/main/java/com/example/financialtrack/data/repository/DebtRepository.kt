package com.example.financialtrack.data.repository

import androidx.lifecycle.LiveData
import com.example.financialtrack.data.database.DebtDao
import com.example.financialtrack.data.model.Debt

class DebtRepository(private val debtDao: DebtDao) {
    
    fun getAllDebts(userId: String): LiveData<List<Debt>> {
        return debtDao.getAllDebts(userId)
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
}
