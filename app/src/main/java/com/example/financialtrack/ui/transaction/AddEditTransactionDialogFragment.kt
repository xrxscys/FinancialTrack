package com.example.financialtrack.ui.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialtrack.R
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.databinding.DialogAddEditTransactionBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.format

class AddEditTransactionDialogFragment {

    //get bound idiot lol; for accessing
    private var _binding: DialogAddEditTransactionBinding? = null
    private val binding get() = _binding!!

    //the transaction that's being edited
    private var transaction: Transaction? = null

    //for when transactions get updated, these get overridden by activity and get called here
    private var listener: TransactionDialogListener? = null

    //interface so that activity can override, and can get accessed from here
    interface TransactionDialogListener{
        fun onTransactionUpdate(transaction: Transaction)
        fun onTransactionDelete(transaction: Transaction)
    }

    companion object{
        //TODO Make Bundle here
    }
}
