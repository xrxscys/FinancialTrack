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

class TransactionAdapter
//    (private val transactions: List<Transaction>)
//    : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder()>
{
    inner class TransactionItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val itemTransactionAmount: TextView = itemView.findViewById(R.id.itemTransAmount)
        private val itemTransactionDate: TextView = itemView.findViewById(R.id.itemTransDate)
        private val itemTransactionName: TextView = itemView.findViewById(R.id.itemTransHeader)

    }
}
