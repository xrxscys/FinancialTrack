package com.example.financialtrack.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.financialtrack.data.model.FinancialGoal
import com.example.financialtrack.databinding.ItemFinancialGoalBinding
import java.text.NumberFormat
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.example.financialtrack.R
import java.text.SimpleDateFormat
import java.util.Locale

class FinancialGoalAdapter : ListAdapter<FinancialGoal, FinancialGoalAdapter.GoalViewHolder>(GoalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemFinancialGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GoalViewHolder(private val binding: ItemFinancialGoalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(goal: FinancialGoal) {
            binding.tvGoalName.text = goal.goalName

            val date = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            binding.tvDeadline.text = date.format(goal.deadline)


            val format = NumberFormat.getCurrencyInstance()
            binding.tvGoalAmounts.text = "${format.format(goal.savedAmount)} / ${format.format(goal.targetAmount)}"

            val progress = if (goal.targetAmount > 0) (goal.savedAmount / goal.targetAmount * 100).toInt() else 0
            binding.pbGoalProgress.progress = progress

            val context = binding.root.context

            val progressColor = when {
                progress >= 100 -> ContextCompat.getColor(context, R.color.progress_complete)
                progress >= 50 -> ContextCompat.getColor(context, R.color.progress_medium)
                else -> ContextCompat.getColor(context, R.color.progress_starting)
            }
            binding.pbGoalProgress.progressTintList = ColorStateList.valueOf(progressColor)
        }
    }

    class GoalDiffCallback : DiffUtil.ItemCallback<FinancialGoal>() {
        override fun areItemsTheSame(oldItem: FinancialGoal, newItem: FinancialGoal) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FinancialGoal, newItem: FinancialGoal) = oldItem == newItem
    }
}
