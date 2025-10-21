package com.example.financialtrack.ui.debt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialtrack.R
import com.example.financialtrack.data.model.Debt
import com.example.financialtrack.data.model.DebtType
import java.text.SimpleDateFormat
import java.util.*

class DebtAdapter(private val debts: List<Debt>) :
    RecyclerView.Adapter<DebtAdapter.DebtViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_debt, parent, false)
        return DebtViewHolder(view)
    }

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) {
        val debt = debts[position]
        holder.bind(debt)
    }

    override fun getItemCount(): Int = debts.size

    inner class DebtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val creditorNameText: TextView = itemView.findViewById(R.id.textCreditorName)
        private val amountText: TextView = itemView.findViewById(R.id.textAmount)
        private val amountPaidText: TextView = itemView.findViewById(R.id.textAmountPaid)
        private val dueDateText: TextView = itemView.findViewById(R.id.textDueDate)
        private val typeText: TextView = itemView.findViewById(R.id.textDebtType)

        fun bind(debt: Debt) {
            creditorNameText.text = debt.creditorName
            amountText.text = "₱${debt.amount}"
            amountPaidText.text = "Paid: ₱${debt.amountPaid}"
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dueDateText.text = "Due: ${sdf.format(Date(debt.dueDate))}"
            typeText.text = when (debt.type) {
                DebtType.LOAN -> "Loan"
                DebtType.DEBT -> "Debt"
                DebtType.CREDIT_CARD -> "Credit Card"
            }
        }
    }
}
