package com.example.financialtrack.ui.transaction

import android.os.Bundle
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import com.example.financialtrack.databinding.DialogAddEditTransactionBinding
import java.util.*


class AddEditTransactionDialogFragment(transaction: Transaction) : DialogFragment(){

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
            val fragment = AddEditTransactionDialogFragment(transaction)
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

    //reconstructs the transaction from a bundle; called on creation of a dialog
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        //uses the keys to retrieve data from the bundle.
        arguments?.let {args ->
            val id = args.getLong(ARG_ID)
            val userId = args.getString(ARG_USER_ID)?: ""
            val typeString = args.getString(ARG_TYPE)?: TransactionType.EXPENSE.name
            val type = TransactionType.valueOf(typeString)
            val category = args.getString(ARG_CATEGORY)?: ""
            val description = args.getString(ARG_DESCRIPTION)?: ""
            val amount = args.getDouble(ARG_AMOUNT, 0.0)
            val date = args.getLong(ARG_DATE, System.currentTimeMillis())

            //reforms the data from the bundle into a Transaction object
            transaction = Transaction(
                id = id,
                userId = userId,
                type = type,
                category = category,
                description = description,
                amount = amount,
                date = date
            )
        }
    }
    //creates and inflates
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogAddEditTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }
    //function calls on view creation
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
        populateFields()
        setupClickListeners()
    }

    private fun setupDialog(){
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,  // Full width
            ViewGroup.LayoutParams.WRAP_CONTENT   // Height adjusts to content
        )
    }
    private fun populateFields(){
        transaction?.let { trans ->
            binding.etAmount.setText(trans.amount.toString())
            binding.etDescription.setText(trans.description)
            binding.etCategory.setText(trans.category)
            val date = Date(trans.date)
            val dateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            binding.tvDate.text = "Date: ${dateFormatter.format(date)}"
        }
    }

    private fun setupClickListeners(){
        binding.btnSave.setOnClickListener {
            saveTransaction()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnDelete.setOnClickListener{
            deleteTransaction()
        }
    }

    private fun saveTransaction(){
        dismiss()
    }

    private fun deleteTransaction(){
        dismiss()
    }
}
