package com.example.financialtrack.ui.transaction

import android.os.Bundle
import android.app.Dialog
import androidx.fragment.app.DialogFragment
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.databinding.DialogAddEditTransactionBinding
import java.util.*


class AddEditTransactionDialogFragment: DialogFragment(){

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
        //keys
        private const val ARG_ID = "id"
        private const val ARG_USER_ID = "userId"
        private const val ARG_TYPE = "type"
        private const val ARG_CATEGORY = "category"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_AMOUNT ="amount"
        private const val ARG_DATE = "date"

        //parameters contain the transaction to be edited
        fun newInstance(transaction: Transaction): AddEditTransactionDialogFragment {
            val fragment = AddEditTransactionDialogFragment()
            val args = Bundle()

            //from the transaction into the bundle
            args.putLong(ARG_ID, transaction.id)
            args.putString(ARG_USER_ID, transaction.userId)
            args.putString(ARG_TYPE, transaction.type.name) // Store enum as string
            args.putString(ARG_CATEGORY, transaction.category)
            args.putString(ARG_DESCRIPTION, transaction.description)
            args.putDouble(ARG_AMOUNT, transaction.amount)
            args.putLong(ARG_DATE, transaction.date)

            fragment.arguments = args
            //returns a fragment with all the data from the transaction being edited.
            return fragment
        }

    }
}
