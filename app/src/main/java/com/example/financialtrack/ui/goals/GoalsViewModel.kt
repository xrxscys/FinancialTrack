package com.example.financialtrack.ui.goals

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.FinancialGoal
import com.example.financialtrack.data.model.GoalStatus
import com.example.financialtrack.data.repository.FinancialGoalRepository
import com.google.firebase.auth.FirebaseAuth

class GoalsViewModel(application: Application) : AndroidViewModel(application) {
    private val transactionDao = AppDatabase.getDatabase(application).transactionDao()
    private val transactionViewModel = com.example.financialtrack.ui.transaction.TransactionViewModel(application)

    // Returns a LiveData<Double> representing the sum of all transfer amounts to this goal
    fun getSavedAmountForGoal(goalId: Int): LiveData<Double> {
        return transactionViewModel.getTransactionsByTransferTarget(goalId, com.example.financialtrack.data.model.TransferTargetType.GOAL)
            .map { txns -> txns.sumOf { it.amount ?: 0.0 } }
    }
    
    // Update expired goals from a provided list
    fun updateExpiredGoals(goals: List<FinancialGoal>) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            goals.filter { it.status == GoalStatus.ACTIVE && it.deadline < now }.forEach { expiredGoal ->
                goalRepository.update(expiredGoal.copy(status = GoalStatus.EXPIRED))
            }
        }
    }
    // Add a new goal
    fun addGoal(goal: FinancialGoal) {
        viewModelScope.launch {
            goalRepository.insert(goal)
        }
    }
    val goalRepository: FinancialGoalRepository
    val userId: String

    init {
        val db = AppDatabase.getDatabase(application)
        goalRepository = FinancialGoalRepository(db.financialGoalDao())
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    fun getGoalsByStatus(vararg statuses: GoalStatus): LiveData<List<FinancialGoal>> {
        return goalRepository.getGoalsByUser(userId).map { goals: List<FinancialGoal> ->
            goals.filter { it.status in statuses }
        }
    }
}
