package com.example.financialtrack.ui.budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialtrack.R
import com.example.financialtrack.data.model.Budget
import com.example.financialtrack.data.model.BudgetPeriod

class BudgetAdapter (private var budgets: List<Budget>) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {
    // TODO: make dialog for creation. make it write to db, make delete, check if display correct
    // TODO: make budget percent and days remaining, change db to have current amount and amount limit

    //just in case I'll need it later
    private var budgetClickListener: ((Budget) -> Unit)? = null

    fun setOnClickListener(listener: (Budget) -> Unit) {
        budgetClickListener = listener
    }

    class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemBudgetCategory: TextView = itemView.findViewById(R.id.tvBudgetCategory)
        val itemBudgetAmount: TextView = itemView.findViewById(R.id.tvBudgetAmount)
        val itemBudgetPercentage: TextView = itemView.findViewById(R.id.tvBudgetPercentage)
        val itemBudgetDaysRemaining: TextView = itemView.findViewById(R.id.tvDaysRemaining)
        val itemBudgetPeriod: TextView = itemView.findViewById(R.id.tvBudgetPeriod)

        fun bind(budget: Budget) {
            itemBudgetCategory.text = budget.category
            itemBudgetAmount.text = "₱${budget.amount}"
//            itemBudgetPercentage.text = "${budget.percentageUsed}%" this is a percentage of total taken up by current
//            itemBudgetDaysRemaining.text = "${budget.daysRemaining} days remaining" this is last day subtracted by current day
            itemBudgetPeriod.text = when (budget.period) {
                BudgetPeriod.DAILY -> "Daily"
                BudgetPeriod.WEEKLY -> "Weekly"
                BudgetPeriod.MONTHLY -> "Monthly"
                BudgetPeriod.YEARLY -> "Yearly"
            }
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
            budgetClickListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int = budgets.size

}
