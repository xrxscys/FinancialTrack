package com.example.financialtrack.ui.budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialtrack.R
import com.example.financialtrack.data.model.Budget
import com.example.financialtrack.data.model.BudgetPeriod
import com.example.financialtrack.data.model.Transaction

class BudgetAdapter (private var budgets: List<Pair<Budget, Double>>) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {
    // TODO: make dialog for creation. make it write to db, make delete, check if display correct
    // TODO: make budget percent and days remaining, change db to have current amount and amount limit

    //just in case I'll need it later
    private var budgetClickListener: ((Budget) -> Unit)? = null

    fun setOnClickListener(listener: (Budget) -> Unit) {
        budgetClickListener = listener
    }

    fun updateBudgets(newBudgets: List<Pair<Budget, Double>>){
        budgets = newBudgets
        notifyDataSetChanged()
    }

    class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemBudgetCategory: TextView = itemView.findViewById(R.id.tvBudgetCategory)
        val itemBudgetAmount: TextView = itemView.findViewById(R.id.tvBudgetAmount)
        val itemBudgetPercentage: TextView = itemView.findViewById(R.id.tvBudgetPercentage)
        val itemBudgetDaysRemaining: TextView = itemView.findViewById(R.id.tvDaysRemaining)
        val itemBudgetPeriod: TextView = itemView.findViewById(R.id.tvBudgetPeriod)
        val itemBudgetProgress: ProgressBar = itemView.findViewById(R.id.progressBudget)

        fun bind(item : Pair<Budget, Double>) {
            itemBudgetCategory.text = item.first.category
            itemBudgetAmount.text = "₱${item.second.toInt()} / ${item.first.amount.toInt()}"
            itemBudgetPercentage.text = "${calculatePercentage(item.first.amount, item.second)}%" //this is a percentage of total taken up by current
            itemBudgetDaysRemaining.text = "${calculateDaysRemaining(item.first)} days remaining" //this is last day subtracted by current day
            itemBudgetPeriod.text = when (item.first.period) {
                BudgetPeriod.DAILY -> "Daily"
                BudgetPeriod.WEEKLY -> "Weekly"
                BudgetPeriod.MONTHLY -> "Monthly"
                BudgetPeriod.YEARLY -> "Yearly"
            }
            itemBudgetProgress.progress = calculatePercentage(item.first.amount, item.second).toInt()
        }

        private fun calculateDaysRemaining(budget: Budget): Int {
            val currentDate = System.currentTimeMillis()
            val daysRemaining = (budget.endDate - currentDate) / (1000 * 60 * 60 * 24)
            return daysRemaining.toInt()
        }

        private fun calculatePercentage(limit: Double, current: Double): Double{
            val perc: Double
            if (current >= 0){
                perc = (current / limit) * 100
            } else {
                perc = 0.0
            }
            return perc
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val item = budgets[position]
        holder.bind(item)

        //as above, so below
        holder.itemView.setOnClickListener {
            budgetClickListener?.invoke(item.first)
        }
    }

    override fun getItemCount(): Int = budgets.size

}
