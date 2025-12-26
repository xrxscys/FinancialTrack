package com.example.financialtrack.ui.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.Notification
import com.example.financialtrack.data.repository.NotificationRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for managing notifications
 * Handles all database operations related to notifications
 * Provides LiveData for UI observation
 */
class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NotificationRepository

    init {
        val notificationDao = AppDatabase.getDatabase(application).notificationDao()
        repository = NotificationRepository(notificationDao)
    }

    /**
     * Get all notifications for a user, ordered by newest first
     */
    fun getAllNotifications(userId: String): LiveData<List<Notification>> {
        return repository.getAllNotifications(userId)
    }

    /**
     * Get only unread notifications for a user
     */
    fun getUnreadNotifications(userId: String): LiveData<List<Notification>> {
        return repository.getUnreadNotifications(userId)
    }

    /**
     * Insert a new notification into the database
     * Runs on background thread via viewModelScope
     */
    fun insertNotification(notification: Notification) = viewModelScope.launch {
        repository.insert(notification)
    }

    /**
     * Mark a notification as read
     * Updates the isRead flag to prevent duplicate reads
     */
    fun markAsRead(id: Long) = viewModelScope.launch {
        repository.markAsRead(id)
    }

    /**
     * Delete a notification
     * Removes the notification from the database permanently
     */
    fun deleteNotification(notification: Notification) = viewModelScope.launch {
        repository.delete(notification)
    }

    /**
     * Delete all notifications for a user
     * Use with caution - this is permanent
     */
    fun deleteAllUserNotifications(userId: String) = viewModelScope.launch {
        repository.deleteAllUserNotifications(userId)
    }
}
