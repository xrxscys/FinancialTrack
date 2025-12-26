package com.example.financialtrack.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.Budget
import com.example.financialtrack.data.model.BudgetPeriod
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import com.example.financialtrack.data.repository.AccountRepository
import com.example.financialtrack.data.repository.BudgetRepository
import com.example.financialtrack.data.repository.TransactionRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val userId: String = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    private val db = AppDatabase.getDatabase(application)
    private val transactionRepo = TransactionRepository(db.transactionDao())
    private val accountRepo = AccountRepository(db.accountDao())
    private val budgetRepo = BudgetRepository(db.budgetDao())

    val totalBalance = MediatorLiveData<Double>()
    val monthIncome = MediatorLiveData<Double>()
    val monthExpense = MediatorLiveData<Double>()
    val recentTransactions = MediatorLiveData<List<Transaction>>()

    val budgetUsedPercent = MediatorLiveData<Int>()
    val budgetPercentLabel = MediatorLiveData<String>()

    private var lastTransactions: List<Transaction> = emptyList()
    private var lastBudgets: List<Budget> = emptyList()

    init {
        // Defaults (safe even if user not logged in)
        totalBalance.value = 0.0
        monthIncome.value = 0.0
        monthExpense.value = 0.0
        recentTransactions.value = emptyList()
        budgetUsedPercent.value = 0
        budgetPercentLabel.value = "—"

        // Only attach DB observers if logged in
        if (userId.isNotBlank()) {

            val accountsLd = accountRepo.getAccountsByUser(userId)
            totalBalance.addSource(accountsLd) { accounts ->
                totalBalance.value = accounts.sumOf { it.balance }
            }

            val transactionsLd: LiveData<List<Transaction>> = transactionRepo.getAllTransactions(userId)
            // observe transactions once, update everything
            monthIncome.addSource(transactionsLd) { tx ->
                lastTransactions = tx ?: emptyList()
                recomputeFromTransactions()
                recomputeBudget()
            }
            monthExpense.addSource(transactionsLd) { tx ->
                lastTransactions = tx ?: emptyList()
                recomputeFromTransactions()
                recomputeBudget()
            }
            recentTransactions.addSource(transactionsLd) { tx ->
                lastTransactions = tx ?: emptyList()
                recomputeFromTransactions()
            }

            val budgetsLd: LiveData<List<Budget>> = budgetRepo.getAllBudgets(userId)
            budgetUsedPercent.addSource(budgetsLd) { budgets ->
                lastBudgets = budgets ?: emptyList()
                recomputeBudget()
            }
            budgetPercentLabel.addSource(budgetsLd) { budgets ->
                lastBudgets = budgets ?: emptyList()
                recomputeBudget()
            }
        }
    }

    private fun recomputeFromTransactions() {
        val (start, endExclusive) = currentMonthRange()

        val monthTx = lastTransactions.filter { it.date in start until endExclusive }

        val income = monthTx
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val expense = monthTx
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        monthIncome.value = income
        monthExpense.value = expense

        recentTransactions.value = lastTransactions
            .sortedByDescending { it.date }
            .take(5)
    }

    private fun recomputeBudget() {
        val (start, endExclusive) = currentMonthRange()

        val monthlyBudgetTotal = lastBudgets
            .filter { it.period == BudgetPeriod.MONTHLY }
            .filter { it.startDate < endExclusive && it.endDate >= start }
            .sumOf { it.amount }

        val expenseThisMonth = monthExpense.value ?: 0.0

        if (monthlyBudgetTotal <= 0.0) {
            budgetUsedPercent.value = 0
            budgetPercentLabel.value = "—"
            return
        }

        val usedPercent = ((expenseThisMonth / monthlyBudgetTotal) * 100.0)
            .toInt()
            .coerceIn(0, 100)

        budgetUsedPercent.value = usedPercent
        budgetPercentLabel.value = "$usedPercent%"
    }

    private fun currentMonthRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()

        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.add(Calendar.MONTH, 1)
        val endExclusive = cal.timeInMillis

        return start to endExclusive
    }
}
