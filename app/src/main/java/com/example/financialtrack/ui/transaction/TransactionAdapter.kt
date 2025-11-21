package com.example.financialtrack.ui.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialtrack.R
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val transactions: List<Transaction>)
    : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>()
{
    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val itemTransactionDate: TextView = itemView.findViewById(R.id.itemTransDate)
        val itemTransactionName: TextView = itemView.findViewById(R.id.itemTransHeader)
        val itemTransactionAmount: TextView = itemView.findViewById(R.id.itemTransAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_compact,parent,false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.itemTransactionName.text = transaction.description
        holder.itemTransactionAmount.text = transaction.amount.toString()
        holder.itemTransactionDate.text = transaction.date.toString()
    }

    override fun getItemCount(): Int = transactions.size
}
