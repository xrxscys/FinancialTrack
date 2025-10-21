package com.example.financialtrack.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialtrack.R

class NotificationAdapter(
    private val notifications: List<String>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNotificationTitle: TextView = itemView.findViewById(R.id.tvNotificationTitle)
        val tvNotificationMessage: TextView = itemView.findViewById(R.id.tvNotificationMessage)
        val tvNotificationDate: TextView = itemView.findViewById(R.id.tvNotificationDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.tvNotificationTitle.text = notification
        holder.tvNotificationMessage.text = "You have a new update"
        holder.tvNotificationDate.text = "Today"
    }

    override fun getItemCount(): Int = notifications.size
}
