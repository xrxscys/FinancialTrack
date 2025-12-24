package com.example.financialtrack.data.repository

import androidx.lifecycle.LiveData
import com.example.financialtrack.data.database.NotificationDao
import com.example.financialtrack.data.model.Notification

class NotificationRepository(private val notificationDao: NotificationDao) {
    
    /**
     * Get all notifications for user, ordered by newest first
     * Already deduplicated at database level via query
     */
    fun getAllNotifications(userId: String): LiveData<List<Notification>> {
        return notificationDao.getAllNotifications(userId)
    }
    
    fun getUnreadNotifications(userId: String): LiveData<List<Notification>> {
        return notificationDao.getUnreadNotifications(userId)
    }
    
    suspend fun getNotificationById(id: Long): Notification? {
        return notificationDao.getNotificationById(id)
    }
    
    /**
     * Insert notification with deduplication logic
     * 
     * PREVENTS DUPLICATES BY:
     * 1. Checking if debt notification already exists for this debtId
     * 2. Only inserting if unique
     * 3. Using IGNORE strategy to handle race conditions
     * 4. Returning -1 if duplicate detected to caller
     * 
     * @return ID of inserted notification, or -1 if duplicate prevented
     */
    suspend fun insert(notification: Notification): Long {
        // For debt notifications, check if one already exists for this debt
        if (notification.debtId != null) {
            val existing = notificationDao.findExistingDebtNotification(
                notification.userId,
                notification.debtId
            )
            // If exists, don't insert duplicate
            if (existing != null) {
                return -1L  // Indicate duplicate prevented
            }
        }
        
        // Insert with IGNORE strategy as secondary guard against race conditions
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
    
    suspend fun deleteAllUserNotifications(userId: String) {
        notificationDao.deleteAllUserNotifications(userId)
    }
}
