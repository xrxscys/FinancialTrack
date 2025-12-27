package com.example.financialtrack.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.financialtrack.data.model.Budget
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE userId = :userId")
    fun getAllBudgets(userId: String): LiveData<List<Budget>>

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getBudgetById(id: Long): Budget?

    @Query("SELECT * FROM budgets WHERE userId = :userId AND category = :category")
    fun getBudgetByCategory(userId: String, category: String): LiveData<Budget?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget): Long

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    @Query("DELETE FROM budgets WHERE userId = :userId")
    suspend fun deleteAllUserBudgets(userId: String)

    @Query("SELECT * FROM transactions WHERE userId = :userId AND category = :category AND type = :type AND date BETWEEN  :startDate AND :endDate")
    suspend fun getTransactionsInRange(userId: String, startDate: Long, endDate: Long, category: String, type: TransactionType = TransactionType.EXPENSE): List<Transaction>
}
