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
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}
