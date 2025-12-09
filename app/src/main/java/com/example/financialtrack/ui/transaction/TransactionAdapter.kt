package com.example.financialtrack.ui.transaction

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.financialtrack.R
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.format
import androidx.core.graphics.toColorInt

class TransactionAdapter(
    private val transactions: MutableList<Transaction>)
    : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>()
{
    private var transactionClickListener : ((Transaction) -> Unit)? = null

    fun setOnClickListener(listener: (Transaction) -> Unit){ //setting up the listener
        transactionClickListener = listener
    }
    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val itemTransactionDate: TextView = itemView.findViewById(R.id.itemTransDate)
        val itemTransactionName: TextView = itemView.findViewById(R.id.itemTransHeader)
        val itemTransactionAmount: TextView = itemView.findViewById(R.id.itemTransAmount)
        val transactionLayout: LinearLayout = itemView.findViewById(R.id.itemTransLayout)
        fun bind(transaction: Transaction){
            //Date format
            val date = Date(transaction.date)
            val dateFormatter = SimpleDateFormat("MM/dd/yyyy",Locale.getDefault())
            val formattedDate = dateFormatter.format(date)

            //set values on layout
            itemTransactionName.text = transaction.description
            itemTransactionAmount.text = "₱${transaction.amount}"
            itemTransactionDate.text = formattedDate

            when (transaction.type) {
                TransactionType.EXPENSE -> {
                    transactionLayout.setBackgroundColor("#F6A6A6".toColorInt())
                }
                TransactionType.INCOME -> {
                    transactionLayout.setBackgroundColor("#BCF0CE".toColorInt())
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_compact,parent,false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = transactions[position] //stores the position of the item in a reusable var
        holder.bind(item)

        // setting click listener on the item view as they bind
        holder.itemView.setOnClickListener {
            transactionClickListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int = transactions.size
}
