package com.example.financialtrack.ui.dashboard

import androidx.lifecycle.*
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import com.example.financialtrack.data.repository.TransactionRepository
import java.util.*

class DashboardViewModel(private val repo: TransactionRepository, private val userId: String) : ViewModel() {

    private val _income = MutableLiveData<Double>()
    val income: LiveData<Double> = _income

    private val _expense = MutableLiveData<Double>()
    val expense: LiveData<Double> = _expense

    private val _categories = MutableLiveData<Map<String, Double>>()
    val categories: LiveData<Map<String, Double>> = _categories

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        repo.getAllTransactions(userId).observeForever { trans ->
            _transactions.value = trans
            calculateTotals(trans)
        }
    }

    private fun calculateTotals(transactions: List<Transaction>) {
        var totalIncome = 0.0
        var totalExpense = 0.0
        val categoryMap = mutableMapOf<String, Double>()

        // Filter transactions for current month
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val monthlyTransactions = transactions.filter {
            calendar.timeInMillis = it.date
            calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == currentYear
        }

        for (t in monthlyTransactions) {
            when (t.type) {
                TransactionType.INCOME -> totalIncome += t.amount
                TransactionType.EXPENSE -> totalExpense += t.amount
                TransactionType.TRANSFER -> { /* skip for dashboard totals */ }
            }

            if (t.type != TransactionType.TRANSFER) {
                categoryMap[t.category] = (categoryMap[t.category] ?: 0.0) + t.amount
            }
        }

        _income.value = totalIncome
        _expense.value = totalExpense
        _categories.value = categoryMap
    }
}
