package com.example.financialtrack.data.repository

import androidx.lifecycle.LiveData
import com.example.financialtrack.data.database.UserDao
import com.example.financialtrack.data.model.User

class UserRepository(private val userDao: UserDao) {
    
    fun getUser(userId: String): LiveData<User?> {
        return userDao.getUser(userId)
    }
    
    suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)
    }
    
    suspend fun insert(user: User) {
        userDao.insert(user)
    }
    
    suspend fun update(user: User) {
        userDao.update(user)
    }
    
    suspend fun delete(user: User) {
        userDao.delete(user)
    }
}
