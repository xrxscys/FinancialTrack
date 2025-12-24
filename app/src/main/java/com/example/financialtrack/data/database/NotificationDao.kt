package com.example.financialtrack.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.financialtrack.data.model.Notification

@Dao
interface NotificationDao {
    
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllNotifications(userId: String): LiveData<List<Notification>>
    
    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadNotifications(userId: String): LiveData<List<Notification>>
    
    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getNotificationById(id: Long): Notification?
    
    /**
     * Check if a debt notification already exists for this debt to prevent duplicates
     * Returns existing notification if found, null otherwise
     */
    @Query("SELECT * FROM notifications WHERE userId = :userId AND debtId = :debtId LIMIT 1")
    suspend fun findExistingDebtNotification(userId: String, debtId: Long): Notification?
    
    /**
     * Insert with IGNORE strategy to prevent duplicate key violations
     * Combined with deduplication logic in repository
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notification: Notification): Long
    
    @Update
    suspend fun update(notification: Notification)
    
    @Delete
    suspend fun delete(notification: Notification)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)
    
    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteAllUserNotifications(userId: String)
}
