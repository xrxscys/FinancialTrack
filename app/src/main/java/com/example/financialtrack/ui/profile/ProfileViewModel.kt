package com.example.financialtrack.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.User
import com.example.financialtrack.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: UserRepository
    
    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }
    
    fun getUser(userId: String): LiveData<User?> {
        return repository.getUser(userId)
    }
    
    fun updateUser(user: User) = viewModelScope.launch {
        repository.update(user)
    }
}
