package com.example.financialtrack.ui.notifications


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.databinding.ActivityNotificationBinding
import com.example.financialtrack.ui.notification.NotificationAdapter // ✅ import

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackNotification.setOnClickListener {
            finish() // Go back to MainActivity
        }


        // Setup RecyclerView with mock notifications
        val mockNotifications = listOf("Payment received", "New message", "Reminder: Budget")
        adapter = NotificationAdapter(mockNotifications)

        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        binding.rvNotifications.adapter = adapter

    }
}
