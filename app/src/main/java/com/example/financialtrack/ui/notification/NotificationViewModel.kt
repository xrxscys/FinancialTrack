package com.example.financialtrack.ui.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.Notification
import com.example.financialtrack.data.repository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: NotificationRepository
    
    init {
        val notificationDao = AppDatabase.getDatabase(application).notificationDao()
        repository = NotificationRepository(notificationDao)
    }
    
    fun getAllNotifications(userId: String): LiveData<List<Notification>> {
        return repository.getAllNotifications(userId)
    }
    
    fun getUnreadNotifications(userId: String): LiveData<List<Notification>> {
        return repository.getUnreadNotifications(userId)
    }
    
    fun insertNotification(notification: Notification) = viewModelScope.launch {
        repository.insert(notification)
    }
    
    fun markAsRead(id: Long) = viewModelScope.launch {
        repository.markAsRead(id)
    }
    
    fun deleteNotification(notification: Notification) = viewModelScope.launch {
        repository.delete(notification)
    }
}
