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
 */
class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private val viewModel: NotificationViewModel by viewModels()
    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupBackButton()
        observeNotifications()
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

    private fun observeNotifications() {
        val userId = currentUserId ?: return

        viewModel.getAllNotifications(userId).observe(this) { notifications ->
            if (notifications.isEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.rvNotifications.visibility = View.GONE
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.rvNotifications.visibility = View.VISIBLE
                // Sort by newest first
                val sortedNotifications = notifications.sortedByDescending { it.createdAt }
                adapter.updateNotifications(sortedNotifications)
            }
        }
    }

    private fun showFeedback(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}
