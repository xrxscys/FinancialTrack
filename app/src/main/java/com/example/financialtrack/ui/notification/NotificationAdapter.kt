package com.example.financialtrack.ui.notification

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialtrack.R
import com.example.financialtrack.data.model.Notification
import com.example.financialtrack.ui.debt.DebtActivity
import com.example.financialtrack.ui.profile.FinancialGoals
import com.example.financialtrack.ui.reports.ReportsActivity
import com.example.financialtrack.ui.transaction.TransactionActivity
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(
    private val notifications: MutableList<Notification>,
    private val onItemClick: ((Notification) -> Unit)? = null,
    private val onDeleteClick: ((Notification) -> Unit)? = null
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cvNotificationCard)
        val tvNotificationTitle: TextView = itemView.findViewById(R.id.tvNotificationTitle)
        val tvNotificationMessage: TextView = itemView.findViewById(R.id.tvNotificationMessage)
        val tvNotificationDate: TextView = itemView.findViewById(R.id.tvNotificationDate)
        val ivReadIndicator: ImageView? = itemView.findViewById(R.id.ivReadIndicator)
        val btnDelete: ImageView? = itemView.findViewById(R.id.btnDeleteNotification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        
        holder.tvNotificationTitle.text = notification.title
        holder.tvNotificationMessage.text = notification.message
        holder.tvNotificationDate.text = formatDate(notification.createdAt)

        // Show/hide read indicator based on isRead status
        holder.ivReadIndicator?.let {
            it.visibility = if (notification.isRead) View.INVISIBLE else View.VISIBLE
        }

        // Handle click navigation
        holder.cardView.setOnClickListener {
            navigateToPage(holder.itemView.context, notification)
            onItemClick?.invoke(notification)
        }

        // Handle delete button
        holder.btnDelete?.setOnClickListener {
            // Start slide out animation
            val slideOutAnimation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_out_right)
            slideOutAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                
                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    // Remove item after animation completes
                    val pos = holder.adapterPosition
                    if (pos >= 0) {
                        notifications.removeAt(pos)
                        notifyItemRemoved(pos)
                        onDeleteClick?.invoke(notification)
                    }
                }
                
                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
            holder.cardView.startAnimation(slideOutAnimation)
        }
    }

    override fun getItemCount(): Int = notifications.size

    /**
     * Update the list of notifications and refresh the adapter
     */
    fun updateNotifications(newNotifications: List<Notification>) {
        notifications.clear()
        notifications.addAll(newNotifications)
        notifyDataSetChanged()
    }

    /**
     * Remove a single notification from the list
     */
    fun removeNotification(notification: Notification) {
        val position = notifications.indexOf(notification)
        if (position >= 0) {
            notifications.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    /**
     * Format the timestamp into a readable date/time string
     */
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Navigate to the appropriate page based on the notification type
     */
    private fun navigateToPage(context: android.content.Context, notification: Notification) {
        val intent = when (notification.navigationType) {
            "transactions" -> Intent(context, TransactionActivity::class.java)
            "debts" -> Intent(context, DebtActivity::class.java)
            "profile" -> Intent(context, FinancialGoals::class.java)
            "reports" -> Intent(context, ReportsActivity::class.java)
            else -> null
        }
        
        if (intent != null) {
            context.startActivity(intent)
        }
    }
}
