package com.example.financialtrack.ui.debt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.financialtrack.R
import com.example.financialtrack.data.model.Debt
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DebtAdapter(
    private val debts: MutableList<Debt>,
    private val isHistory: Boolean = false,
    private val onMarkAsPaid: (Debt) -> Unit = {},
    private val onDelete: ((Debt) -> Unit)? = null
) : RecyclerView.Adapter<DebtAdapter.DebtViewHolder>() {

    private val expandedItems = mutableSetOf<Long>()
    private val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    private val dateOnlySdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_debt, parent, false)
        return DebtViewHolder(view)
    }

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) {
        val debt = debts[position]
        holder.bind(debt, expandedItems.contains(debt.id))
    }

    override fun getItemCount(): Int = debts.size

    fun updateList(newDebts: List<Debt>) {
        debts.clear()
        debts.addAll(newDebts)
        notifyDataSetChanged()
    }

    inner class DebtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val collapsedView: ConstraintLayout = itemView.findViewById(R.id.collapsedView)
        private val expandedView: ConstraintLayout = itemView.findViewById(R.id.expandedView)
        private val expandIcon: ImageView = itemView.findViewById(R.id.expandIcon)

        // Collapsed view elements
        private val loanTitle: TextView = itemView.findViewById(R.id.loanTitle)
        private val loanAmount: TextView = itemView.findViewById(R.id.loanAmount)
        private val loanDeadline: TextView = itemView.findViewById(R.id.loanDeadline)

        // Expanded view elements
        private val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
        private val fullDetailsText: TextView = itemView.findViewById(R.id.fullDetailsText)
        private val markAsPaidCheckbox: CheckBox = itemView.findViewById(R.id.markAsPaidCheckbox)
        private val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)

        fun bind(debt: Debt, isExpanded: Boolean) {
            val isExpandedNow = isExpanded

            // Bind collapsed view
            loanTitle.text = debt.creditorName
            loanAmount.text = "₱${formatAmount(debt.amount)}"
            loanDeadline.text = dateOnlySdf.format(Date(debt.dueDate))

            // Set urgency color
            val daysUntilDeadline = getDaysUntilDeadline(debt.dueDate)
            val urgencyColor = when {
                daysUntilDeadline < 1 -> R.color.urgency_critical // Red for overdue/today
                daysUntilDeadline <= 3 -> R.color.urgency_high    // Orange for 1-3 days
                daysUntilDeadline <= 7 -> R.color.urgency_medium  // Yellow for 1-7 days
                else -> R.color.urgency_low                        // Green for 7+ days
            }
            loanDeadline.setTextColor(ContextCompat.getColor(itemView.context, urgencyColor))

            // Bind expanded view
            expandedView.visibility = if (isExpandedNow) View.VISIBLE else View.GONE
            expandIcon.rotation = if (isExpandedNow) 180f else 0f

            descriptionText.text = if (debt.description.isEmpty())
                "No description provided" else debt.description

            fullDetailsText.text = buildString {
                append("Amount: ₱${formatAmount(debt.amount)}\n")
                append("Remaining: ₱${formatAmount(debt.remainingBalance)}\n")
                append("Due: ${dateOnlySdf.format(Date(debt.dueDate))}\n")
                append("Created: ${dateOnlySdf.format(Date(debt.createdAt))}\n")
                if (debt.paidAt != null) {
                    append("Paid: ${dateOnlySdf.format(Date(debt.paidAt))}\n")
                }
            }

            // Mark as Paid checkbox - REMOVED (loans are marked paid only when amountPaid == amount)
            markAsPaidCheckbox.visibility = View.GONE

            // Delete button - visible for all loans
            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener {
                onDelete?.invoke(debt)
            }

            // Collapse/expand on click
            collapsedView.setOnClickListener {
                if (isExpandedNow) {
                    expandedItems.remove(debt.id)
                } else {
                    expandedItems.add(debt.id)
                }
                notifyItemChanged(adapterPosition)
            }
        }

        private fun formatAmount(amount: Double): String {
            return String.format("%.2f", amount)
        }

        private fun getDaysUntilDeadline(dueDate: Long): Long {
            val deadline = Calendar.getInstance()
            deadline.timeInMillis = dueDate

            val diffTime = dueDate - System.currentTimeMillis()
            return diffTime / (1000 * 60 * 60 * 24)
        }
    }
}
