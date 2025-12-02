package com.example.financialtrack.ui.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialtrack.R
import com.example.financialtrack.data.model.Transaction
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.format

class TransactionAdapter(
    private val transactions: List<Transaction>)
    : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>()
{
//    private var transactionClickListener : ((Transaction) -> Unit)? = null
    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val itemTransactionDate: TextView = itemView.findViewById(R.id.itemTransDate)
        val itemTransactionName: TextView = itemView.findViewById(R.id.itemTransHeader)
        val itemTransactionAmount: TextView = itemView.findViewById(R.id.itemTransAmount)
        //TODO recyclerview clickable
//        init {
//            itemView.setOnClickListener{
//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION){
//                    transactionClickListener?.invoke()
//                }
//            }
//        }
        fun bind(transaction: Transaction){
            //Date format
            val date = Date(transaction.date)
            val dateFormatter = SimpleDateFormat("MM/dd/yyyy",Locale.getDefault())
            val formattedDate = dateFormatter.format(date)

            //set values on layout
            itemTransactionName.text = transaction.description
            itemTransactionAmount.text = "₱${transaction.amount}"
            itemTransactionDate.text = formattedDate

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_compact,parent,false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size
}
