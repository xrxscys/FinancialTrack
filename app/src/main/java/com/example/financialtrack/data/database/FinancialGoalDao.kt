package com.example.financialtrack.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.financialtrack.data.model.FinancialGoal
import com.example.financialtrack.data.model.GoalStatus

@Dao
interface FinancialGoalDao {
    @Query("SELECT * FROM financial_goals WHERE userId = :userId ORDER BY deadline ASC")
    fun getGoalsByUser(userId: String): LiveData<List<FinancialGoal>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: FinancialGoal)

    @Update
    suspend fun update(goal: FinancialGoal)

    @Query("SELECT * FROM financial_goals WHERE userId = :userId AND status = :status ORDER BY deadline ASC")
    fun getGoalsByStatus(userId: String, status: GoalStatus): LiveData<List<FinancialGoal>>
}
