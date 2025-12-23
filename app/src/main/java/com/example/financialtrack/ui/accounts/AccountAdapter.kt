package com.example.financialtrack.ui.accounts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.financialtrack.R
import com.example.financialtrack.data.model.Account
import com.google.android.material.card.MaterialCardView

class AccountAdapter(
    private val onItemClick: (Account) -> Unit
) : ListAdapter<Account, AccountAdapter.AccountViewHolder>(AccountDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_account, parent, false)
        return AccountViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AccountViewHolder(itemView: View, private val onItemClick: (Account) -> Unit ) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.tv_account_name)
        private val typeText: TextView = itemView.findViewById(R.id.tv_account_type)
        private val balanceText: TextView = itemView.findViewById(R.id.tv_account_balance)
        private val card: MaterialCardView = itemView.findViewById(R.id.card)
        private val ivBalanceWarning: ImageView = itemView.findViewById(R.id.iv_balance_warning)
        private val tvBalanceWarning: TextView = itemView.findViewById(R.id.tv_balance_warning)

        fun bind(account: Account) {
            nameText.text = account.name
            typeText.text = account.type.name
            balanceText.text = "Balance: ${account.balance}" // Format as needed

            if (account.balance <= 0) {
                val dangerColor = ContextCompat.getColor(itemView.context, R.color.expense_red)
                card.strokeColor = dangerColor
                balanceText.setTextColor(dangerColor)
                ivBalanceWarning.visibility = View.VISIBLE
                tvBalanceWarning.visibility = View.VISIBLE
            } else {
                card.strokeColor = com.google.android.material.R.color.design_default_color_primary
                balanceText.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))
                ivBalanceWarning.visibility = View.GONE
                tvBalanceWarning.visibility = View.GONE
            }

            itemView.isClickable = true
            itemView.setOnClickListener {
                onItemClick(account)
            }
        }
    }

    class AccountDiffCallback : DiffUtil.ItemCallback<Account>() {
        override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean = oldItem == newItem
    }
}
