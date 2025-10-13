package com.example.financialtrack.ui.budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.Budget
import com.example.financialtrack.data.repository.BudgetRepository
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: BudgetRepository
    
    init {
        val budgetDao = AppDatabase.getDatabase(application).budgetDao()
        repository = BudgetRepository(budgetDao)
    }
    
    fun getAllBudgets(userId: String): LiveData<List<Budget>> {
        return repository.getAllBudgets(userId)
    }
    
    fun getBudgetByCategory(userId: String, category: String): LiveData<Budget?> {
        return repository.getBudgetByCategory(userId, category)
    }
    
    fun insertBudget(budget: Budget) = viewModelScope.launch {
        repository.insert(budget)
    }
    
    fun updateBudget(budget: Budget) = viewModelScope.launch {
        repository.update(budget)
    }
    
    fun deleteBudget(budget: Budget) = viewModelScope.launch {
        repository.delete(budget)
    }
}
