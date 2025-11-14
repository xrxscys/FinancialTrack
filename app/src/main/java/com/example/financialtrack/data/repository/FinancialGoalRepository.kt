package com.example.financialtrack.data.repository

import androidx.lifecycle.LiveData
import com.example.financialtrack.data.database.dao.FinancialGoalDao
import com.example.financialtrack.data.model.FinancialGoal
import com.example.financialtrack.data.model.GoalStatus

class FinancialGoalRepository(private val financialGoalDao: FinancialGoalDao) {

    // Gets all ACTIVE goals for a user
    fun getActiveGoals(userId: String): LiveData<List<FinancialGoal>> {
        return financialGoalDao.getGoalsByStatus(userId, GoalStatus.ACTIVE)
    }

    suspend fun insert(goal: FinancialGoal) {
        financialGoalDao.insert(goal)
    }
}
