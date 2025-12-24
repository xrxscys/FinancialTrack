package com.example.financialtrack.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.databinding.ActivityNotificationBinding
import com.example.financialtrack.ui.notification.NotificationAdapter
import com.example.financialtrack.ui.notification.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * NotificationActivity displays all notifications for the current user
 * Supports viewing, marking as read, and deleting notifications
 * 
 * PREVENTS DUPLICATE OBSERVERS BY:
 * - Removing observer in onDestroy()
 * - Ensuring single observer attachment with observersAttached flag
 * - Activity lifecycle ensures observers aren't recreated unnecessarily
 */
class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private val viewModel: NotificationViewModel by viewModels()
    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid
    
    // Track if we've already attached observers to prevent duplicates
    private var observersAttached = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupBackButton()
        observeNotifications()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up observer flag to prevent memory leaks and duplicate observers on recreate
        observersAttached = false
    }

    private fun setupRecyclerView() {
        adapter = NotificationAdapter(
            mutableListOf(),
            onItemClick = { notification ->
                // Mark as read when clicked
                if (!notification.isRead) {
                    viewModel.markAsRead(notification.id)
                }
            },
            onDeleteClick = { notification ->
                // Delete notification
                viewModel.deleteNotification(notification)
                showFeedback("Notification deleted")
            }
        )

        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        binding.rvNotifications.adapter = adapter
    }

    private fun setupBackButton() {
        binding.btnBackNotification.setOnClickListener {
            finish()
        }
    }

    /**
     * Observe notifications with proper lifecycle management
     * 
     * PREVENTS DUPLICATE OBSERVERS BY:
     * - Only attaching once via observersAttached flag
     * - Checking if already attached before adding new observer
     * - Cleaning up in onDestroy()
     * - Automatic deduplication in adapter.updateNotifications()
     */
    private fun observeNotifications() {
        val userId = currentUserId ?: return
        
        // Guard against multiple observer attachments
        if (observersAttached) {
            return
        }
        observersAttached = true

        viewModel.getAllNotifications(userId).observe(this) { notifications ->
            if (notifications.isEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.rvNotifications.visibility = View.GONE
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.rvNotifications.visibility = View.VISIBLE
                // Sort by newest first - automatic deduplication in adapter.updateNotifications()
                val sortedNotifications = notifications.sortedByDescending { it.createdAt }
                adapter.updateNotifications(sortedNotifications)
            }
        }
    }

    private fun showFeedback(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}
