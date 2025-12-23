package com.example.financialtrack.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.financialtrack.data.model.User

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUser(userId: String): LiveData<User?>
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)
    
    @Update
    suspend fun update(user: User)
    
    @Delete
    suspend fun delete(user: User)
}
