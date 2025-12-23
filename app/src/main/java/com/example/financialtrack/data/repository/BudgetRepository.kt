package com.example.financialtrack.data.repository

import androidx.lifecycle.LiveData
import com.example.financialtrack.data.database.BudgetDao
import com.example.financialtrack.data.model.Budget

class BudgetRepository(private val budgetDao: BudgetDao) {
    
    fun getAllBudgets(userId: String): LiveData<List<Budget>> {
        return budgetDao.getAllBudgets(userId)
    }
    
    fun getBudgetByCategory(userId: String, category: String): LiveData<Budget?> {
        return budgetDao.getBudgetByCategory(userId, category)
    }
    
    suspend fun getBudgetById(id: Long): Budget? {
        return budgetDao.getBudgetById(id)
    }
    
    suspend fun insert(budget: Budget): Long {
        return budgetDao.insert(budget)
    }
    
    suspend fun update(budget: Budget) {
        budgetDao.update(budget)
    }
    
    suspend fun delete(budget: Budget) {
        budgetDao.delete(budget)
    }
}
