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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_compact,parent,false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val date = Date(transaction.date)
        val dateFormatter = SimpleDateFormat("MM/dd/yyyy",Locale.getDefault())
        val formattedDate = dateFormatter.format(date)

        holder.itemTransactionName.text = transaction.description
        holder.itemTransactionAmount.text = transaction.amount.toString()
        holder.itemTransactionDate.text = formattedDate
    }

    override fun getItemCount(): Int = transactions.size
}
