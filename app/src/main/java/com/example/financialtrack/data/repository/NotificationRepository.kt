package com.example.financialtrack.data.repository

import androidx.lifecycle.LiveData
import com.example.financialtrack.data.database.NotificationDao
import com.example.financialtrack.data.model.Notification

class NotificationRepository(private val notificationDao: NotificationDao) {
    
    fun getAllNotifications(userId: String): LiveData<List<Notification>> {
        return notificationDao.getAllNotifications(userId)
    }
    
    fun getUnreadNotifications(userId: String): LiveData<List<Notification>> {
        return notificationDao.getUnreadNotifications(userId)
    }
    
    suspend fun getNotificationById(id: Long): Notification? {
        return notificationDao.getNotificationById(id)
    }
    
    suspend fun insert(notification: Notification): Long {
        return notificationDao.insert(notification)
    }
    
    suspend fun update(notification: Notification) {
        notificationDao.update(notification)
    }
    
    suspend fun delete(notification: Notification) {
        notificationDao.delete(notification)
    }
    
    suspend fun markAsRead(id: Long) {
        notificationDao.markAsRead(id)
    }
}
