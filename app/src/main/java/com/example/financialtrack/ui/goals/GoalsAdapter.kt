package com.example.financialtrack.ui.goals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.financialtrack.R
import com.example.financialtrack.data.model.FinancialGoal

class GoalsAdapter : ListAdapter<FinancialGoal, GoalsAdapter.GoalViewHolder>(GoalDiffCallback()) {
    var onGoalClickListener: ((FinancialGoal) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_financial_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.tv_goal_name)
        private val deadlineText: TextView = itemView.findViewById(R.id.tv_deadline)
        private val amountsText: TextView = itemView.findViewById(R.id.tv_goal_amounts)
        private val statusText: TextView = itemView.findViewById(R.id.tv_status)
        private val progressBar: android.widget.ProgressBar = itemView.findViewById(R.id.pb_goal_progress)

        private fun formatAsPHP(amount: Double): String {
            val format = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
            format.minimumFractionDigits = 2
            format.maximumFractionDigits = 2
            return "₱${format.format(amount)}"
        }

        fun bind(goal: FinancialGoal) {
            nameText.text = goal.goalName
            val date = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault())
            deadlineText.text = date.format(java.util.Date(goal.deadline))

            val savedAmountFormatted = formatAsPHP(goal.savedAmount)
            val targetAmountFormatted = formatAsPHP(goal.targetAmount)
            amountsText.text = "$savedAmountFormatted / $targetAmountFormatted"

            val progress = if (goal.targetAmount > 0) (goal.savedAmount / goal.targetAmount * 100).toInt() else 0
            progressBar.progress = progress

            val context = itemView.context
            val progressColor = when {
                progress >= 100 -> androidx.core.content.ContextCompat.getColor(context, com.example.financialtrack.R.color.progress_complete)
                progress >= 50 -> androidx.core.content.ContextCompat.getColor(context, com.example.financialtrack.R.color.progress_medium)
                else -> androidx.core.content.ContextCompat.getColor(context, com.example.financialtrack.R.color.progress_starting)
            }
            progressBar.progressTintList = android.content.res.ColorStateList.valueOf(progressColor)

            // Show status only for COMPLETED and EXPIRED
            if (goal.status == com.example.financialtrack.data.model.GoalStatus.COMPLETED) {
                statusText.visibility = View.VISIBLE
                statusText.text = "COMPLETED"
                statusText.setTextColor(androidx.core.content.ContextCompat.getColor(context, com.example.financialtrack.R.color.status_completed))
                statusText.setTypeface(null, android.graphics.Typeface.BOLD)
            } else if (goal.status == com.example.financialtrack.data.model.GoalStatus.EXPIRED) {
                statusText.visibility = View.VISIBLE
                statusText.text = "EXPIRED"
                statusText.setTextColor(androidx.core.content.ContextCompat.getColor(context, com.example.financialtrack.R.color.status_expired))
                statusText.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                statusText.visibility = View.GONE
            }

            itemView.setOnClickListener { onGoalClickListener?.invoke(goal) }
        }
    }

    class GoalDiffCallback : DiffUtil.ItemCallback<FinancialGoal>() {
        override fun areItemsTheSame(oldItem: FinancialGoal, newItem: FinancialGoal): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FinancialGoal, newItem: FinancialGoal): Boolean = oldItem == newItem
    }
}
