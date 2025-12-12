package com.example.financialtrack.ui.transaction

import android.app.DatePickerDialog
import android.os.Bundle
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import com.example.financialtrack.databinding.DialogAddEditTransactionBinding
import java.util.*

class AddEditTransactionDialogFragment() : DialogFragment(){

    //get bound idiot lol; for accessing
    private var _binding: DialogAddEditTransactionBinding? = null
    private val binding get() = _binding!!

    //the transaction that's being edited
    private var transaction: Transaction? = null

    //for when transactions get updated, these get overridden by activity and get called here
    private var listener: TransactionDialogListener? = null
    private var isNewTrans = false

    private var selectedDate: Long = System.currentTimeMillis()
    //interface so that activity can override, and can get accessed from here
    interface TransactionDialogListener{
        fun onTransactionUpdate(transaction: Transaction)
        fun onTransactionDelete(transaction: Transaction)
        fun onTransactionCreated(transaction: Transaction)
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
        private const val ARG_IS_NEW = "isNew"

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
            args.putBoolean(ARG_IS_NEW, false)

            fragment.arguments = args
            //returns a fragment with all the data from the transaction being edited.
            return fragment
        }

        fun newInstanceForCreate(userId: String): AddEditTransactionDialogFragment{
            val fragment = AddEditTransactionDialogFragment()
            val args = Bundle()


            args.putString(ARG_USER_ID, userId)
            args.putBoolean(ARG_IS_NEW, true)
            fragment.arguments = args
            return fragment
        }
    }

    //reconstructs the transaction from a bundle; called on creation of a dialog
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        //uses the keys to retrieve data from the bundle.
        arguments?.let {args ->
            isNewTrans = args.getBoolean(ARG_IS_NEW, false)

            if (isNewTrans) {
                val userId = args.getString(ARG_USER_ID)?: ""

                transaction = Transaction(
                    id = 0, //placeholder ID, supposedly the db will assign this
                    userId = userId,
                    type = TransactionType.EXPENSE,
                    category = "",
                    description = "",
                    amount = 0.0,
                    date = System.currentTimeMillis()
                )


            } else{
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

        setupDialog() //sets up the layout of the dialog, it would look weird after, I might just change the xml later
        setupTypeDropdown()
        setupDatePicker()
        populateFields() //puts the data from the transaction reformed from the bundle into the fields
        setupClickListeners() //sets up the listeners for the buttons in the dialog, will do later

        changeDialogIfNew()
    }

    private fun setupDialog(){
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,  // Full width
            ViewGroup.LayoutParams.WRAP_CONTENT   // Height adjusts to content
        )
    }

    private fun changeDialogIfNew(){
        if(isNewTrans){
            binding.btnDelete.visibility = View.GONE
            binding.btnSave.text = "Create"
            binding.tvDialogHead.text = "Create Transaction"
        }else{
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnSave.text = "Save"
            binding.tvDialogHead.text = "Edit Transaction"

        }
    }

    private fun setupTypeDropdown(){
        val types = arrayOf("Expense", "Income") // string array of Transaction Types
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, types)
        binding.actvType.setAdapter(adapter)

        binding.actvType.setText(
            when(transaction?.type){
                TransactionType.INCOME -> "Income"
                else -> "Expense"
            },
            false
        )
    }


    private fun setupDatePicker(){

        updateDatePicker()

        binding.etDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog(){
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Update the selected date
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.timeInMillis
                updateDatePicker()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)

        )

        datePickerDialog.show()

    }

    private fun updateDatePicker(){
        val date = Date(selectedDate)
        val dateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        binding.etDate.setText(dateFormatter.format(date))
    }

    private fun populateFields(){
        transaction?.let { trans ->
            val dateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            if (!isNewTrans){
                binding.etAmount.setText(trans.amount.toString())
                binding.etDescription.setText(trans.description)
                binding.etCategory.setText(trans.category)
                binding.etDate.setText("${dateFormatter.format(trans.date)}")
            } else{
                binding.etDate.setText("${dateFormatter.format(trans.date)}")

            }

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
        val typeString = binding.actvType.text.toString()
        val type = when(typeString){
            "Income" -> TransactionType.INCOME
            else -> TransactionType.EXPENSE
        }

        val description = binding.etDescription.text.toString().trim()
        val amountStr = binding.etAmount.text.toString().trim()
        val amount = amountStr.toDoubleOrNull()
        val category = binding.etCategory.text.toString().trim()


        if (description.isEmpty()){
            Toast.makeText(context, "Please enter description", Toast.LENGTH_SHORT).show()
            return
        }

        if (amountStr.isEmpty()){
            Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show()
            return
        }

        if ((amount == null || amount <= 0)){
            Toast.makeText(context, "Please enter amount that is valid", Toast.LENGTH_SHORT).show()
            return
        }

        if (category.isEmpty()){
            Toast.makeText(context, "Please enter category", Toast.LENGTH_SHORT).show()
            return
        }

        transaction?.let { trans ->
            if (isNewTrans){
                val newTrans = trans.copy(
                    id = 0, //autogenerated by db
                    type = type,
                    description = description,
                    category = category,
                    amount = amount,
                    date = selectedDate,
                )
                listener?.onTransactionCreated(newTrans)
                Toast.makeText(context, "Transaction created", Toast.LENGTH_SHORT).show()
            }else{
                val updated = trans.copy(
                    type = type,
                    description = description,
                    category = category,
                    amount = amount,
                    date = selectedDate,
                )

                listener?.onTransactionUpdate(updated)
                Toast.makeText(context, "Transaction updated", Toast.LENGTH_SHORT).show()
            }
        }

        dismiss()
    }

    private fun deleteTransaction() {
        transaction?.let { trans ->
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete") { _, _ ->
                    listener?.onTransactionDelete(trans)
                    Toast.makeText(context, "Transaction deleted", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }


    fun setListener(listener: TransactionDialogListener){
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
