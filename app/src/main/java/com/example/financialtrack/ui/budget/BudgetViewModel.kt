package com.example.financialtrack.ui.budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.Budget
import com.example.financialtrack.data.model.Transaction
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

    suspend fun getTransactionsInRange(budget: Budget): List<Transaction?>{
        return repository.getTransactionsInRange(budget)
    }

    fun getAmountSpentInRange(userId: String): LiveData<List<Pair<Budget, Double>>>{
        val budgetLD = getAllBudgets(userId)
        return budgetLD.switchMap { budgetList ->
            liveData {
                val amountSpentList = budgetList.map { budget ->
                    val transactions = repository.getTransactionsInRange(budget)
                    var amountSpent = 0.0
                    for (transaction in transactions){
                        amountSpent += transaction.amount
                    }
                    Pair(budget, amountSpent)
                }
                emit(amountSpentList)
            }

        }
    }
}

