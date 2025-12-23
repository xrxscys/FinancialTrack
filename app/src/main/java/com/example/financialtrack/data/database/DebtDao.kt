package com.example.financialtrack.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.financialtrack.data.model.Debt

@Dao
interface DebtDao {
    
    @Query("SELECT * FROM debts WHERE userId = :userId ORDER BY dueDate ASC")
    fun getAllDebts(userId: String): LiveData<List<Debt>>
    
    @Query("SELECT * FROM debts WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
    suspend fun getActiveDebts(userId: String): List<Debt>
    
    @Query("SELECT * FROM debts WHERE userId = :userId AND isActive = 0 ORDER BY paidAt DESC")
    suspend fun getPaidDebts(userId: String): List<Debt>
    
    @Query("SELECT * FROM debts WHERE id = :id")
    suspend fun getDebtById(id: Long): Debt?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: Debt): Long
    
    @Update
    suspend fun update(debt: Debt)
    
    @Delete
    suspend fun delete(debt: Debt)
    
    @Query("DELETE FROM debts WHERE userId = :userId")
    suspend fun deleteAllUserDebts(userId: String)
}
