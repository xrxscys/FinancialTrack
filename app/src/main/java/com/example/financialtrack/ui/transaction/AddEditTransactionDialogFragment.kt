package com.example.financialtrack.ui.transaction

import android.app.DatePickerDialog
import android.os.Bundle
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.example.financialtrack.data.database.AppDatabase
import androidx.fragment.app.viewModels
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import com.example.financialtrack.data.model.Account
import com.example.financialtrack.data.model.TransferTargetType
import com.example.financialtrack.data.model.Debt
import com.example.financialtrack.databinding.DialogAddEditTransactionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AddEditTransactionDialogFragment() : DialogFragment(){
    //for combining accounts and goals in transfer to
    private val goalsViewModel: com.example.financialtrack.ui.goals.GoalsViewModel by viewModels()
    private var activeGoals: List<com.example.financialtrack.data.model.FinancialGoal> = emptyList()


    private val viewModel: TransactionViewModel by viewModels()

    private var accountList: List<Account> = emptyList()


    //get bound idiot lol; for accessing
    private var _binding: DialogAddEditTransactionBinding? = null
    private val binding get() = _binding!!

    //the transaction that's being edited
    private var transaction: Transaction? = null

    //for when transactions get updated, these get overridden by activity and get called here
    private var listener: TransactionDialogListener? = null
    private var isNewTrans = false

    private var selectedDate: Long = System.currentTimeMillis()
    private var activeLoans: List<Debt> = emptyList()
    private var selectedLoanId: Long? = null
    //interface so that activity can override, and can get accessed from here
    interface TransactionDialogListener{
        fun onTransactionUpdate(transaction: Transaction)
        fun onTransactionDelete(transaction: Transaction)
        fun onTransactionCreated(transaction: Transaction, selectedLoanId: Long? = null)
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
        private const val ARG_ACCOUNT_ID = "accountId"
        private const val ARG_TRANSFER_TO_ID = "transferToId"
        private const val ARG_TRANSFER_TO_TYPE = "transferToType"

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
            args.putInt(ARG_ACCOUNT_ID, transaction.accountId)
            args.putInt(ARG_TRANSFER_TO_ID, transaction.transferToId)
            //transfertype to string if not null
            args.putString(ARG_TRANSFER_TO_TYPE, transaction.transferToType?.name)


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
                    date = System.currentTimeMillis(),
                    accountId = 0,
                    transferToId = 0,
                    transferToType = null
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
                val accountId = args.getInt(ARG_ACCOUNT_ID, 0)
                val transferToId = args.getInt(ARG_TRANSFER_TO_ID, 0)
                val transferToTypeString = args.getString(ARG_TRANSFER_TO_TYPE)
                val transferToType = transferToTypeString?.let { TransferTargetType.valueOf(it) }
                //reforms the data from the bundle into a Transaction object
                transaction = Transaction(
                    id = id,
                    userId = userId,
                    type = type,
                    category = category,
                    description = description,
                    amount = amount,
                    date = date,
                    accountId = accountId,
                    transferToId = transferToId,
                    transferToType = transferToType
                )
            }
            viewModel.getAllAccounts(userId = transaction!!.userId).observe(this){
                accounts ->
                accountList = accounts
                setupTypeDropdown()
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
        setupDatePicker()
        populateFields() //puts the data from the transaction reformed from the bundle into the fields
        setupClickListeners() //sets up the listeners for the buttons in the dialog, will do later
        populateLoans() //loads active loans for the dropdown

        changeDialogIfNew()
        setupTypeChangeListener()
    }

    private fun setupTypeChangeListener() {
        binding.actvType.addTextChangedListener { text ->
            val selectedType = text.toString()

            when (selectedType) {
                "Transfer" -> {
                    // Hide category, show transfer to
                    binding.tilCategory.visibility = View.GONE
                    binding.tilTransferTo.visibility = View.VISIBLE
                }
                else -> {
                    // Show category, hide transfer to
                    binding.tilCategory.visibility = View.VISIBLE
                    binding.tilTransferTo.visibility = View.GONE
                }
            }
        }

        // Trigger initial state
        if (transaction?.type == TransactionType.TRANSFER) {
            binding.tilCategory.visibility = View.GONE
            binding.tilTransferTo.visibility = View.VISIBLE
        }
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
        val types = arrayOf("Expense", "Income", "Transfer")
        val accounts = accountList.map { "Account: ${it.name}" }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, types)
        binding.actvType.setAdapter(adapter)
        binding.actvType.setText(
            when(transaction?.type){
                TransactionType.INCOME -> "Income"
                TransactionType.EXPENSE -> "Expense"
                else -> "Transfer"
            },
            false
        )
        binding.actvAccount.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, accountList.map { it.name }))
        if (accountList.isNotEmpty()){
            val currAcc = accountList.find { it.id == transaction?.accountId }
            if (currAcc != null){
                binding.actvAccount.setText(currAcc.name, false)
            }
        }
        // Observe active goals and combine with accounts for transfer-to
        val userId = transaction?.userId ?: ""
        goalsViewModel.goalRepository.getGoalsByUser(userId).observe(this) { allGoals ->
            val unarchivedGoals = allGoals.filter { !it.isArchived }
            activeGoals = unarchivedGoals
            val goalNames = unarchivedGoals.map { "Goal: ${it.goalName}" }
            val combined = accounts + goalNames
            val transferAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, combined)
            binding.actvTransferTo.setAdapter(transferAdapter)
            // Set current selection if editing
            if (transaction?.transferToId != null && transaction?.transferToId != 0 && transaction?.transferToType != null) {
                when (transaction?.transferToType) {
                    TransferTargetType.ACCOUNT -> {
                        val acc = accountList.find { it.id == transaction?.transferToId }
                        if (acc != null) {
                            binding.actvTransferTo.setText("Account: ${acc.name}", false)
                        }
                    }
                    TransferTargetType.GOAL -> {
                        val goal = unarchivedGoals.find { it.id == transaction?.transferToId }
                        if (goal != null) {
                            binding.actvTransferTo.setText("Goal: ${goal.goalName}", false)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun populateLoans() {
        val userId = transaction?.userId ?: "user123"
        val database = AppDatabase.getDatabase(requireContext())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val activeLoans = database.debtDao().getActiveDebts(userId)

                if (activeLoans.isEmpty()) {
                    Log.d("LoanDropdown", "No active loans found")
                }

                this@AddEditTransactionDialogFragment.activeLoans = activeLoans

                val loanOptions = mutableListOf("None")
                loanOptions.addAll(activeLoans.map { it.creditorName })

                runOnUiThread {
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        loanOptions
                    )
                    binding.actvLoanPayment.setAdapter(adapter)
                    binding.actvLoanPayment.setText("None", false)

                    binding.actvLoanPayment.setOnItemClickListener { _, _, position, _ ->
                        selectedLoanId = if (position == 0) null else activeLoans.getOrNull(position - 1)?.id
                    }
                }
            } catch (e: Exception) {
                Log.e("LoanDropdown", "Error loading loans: ${e.message}", e)
            }
        }
    }

    private fun runOnUiThread(action: () -> Unit) {
        binding.root.post(action)
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
            "Expense" -> TransactionType.EXPENSE
            else -> TransactionType.TRANSFER
        }

        val description = binding.etDescription.text.toString().trim()
        val amountStr = binding.etAmount.text.toString().trim()
        val amount = amountStr.toDoubleOrNull()
        var category: String
        val currAccName = binding.actvAccount.text.toString()
        val currAcc = accountList.find { it.name == currAccName }
        var transferId: Int
        var transferType: TransferTargetType? = null

        if (currAcc?.id == null || currAcc.id == 0){
            Toast.makeText(context, "Please enter valid account", Toast.LENGTH_SHORT).show()
            return
        }

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

        if(type == TransactionType.TRANSFER){
            val transferToText = binding.actvTransferTo.text.toString()
            category = "Transfer"
            var foundId: Int? = null
            if (transferToText.startsWith("Account: ")) {
                val accName = transferToText.removePrefix("Account: ").trim()
                foundId = accountList.find { it.name == accName }?.id
                transferType = TransferTargetType.ACCOUNT
            } else if (transferToText.startsWith("Goal: ")) {
                val goalName = transferToText.removePrefix("Goal: ").trim()
                foundId = activeGoals.find { it.goalName == goalName }?.id
                transferType = TransferTargetType.GOAL
            }
            if(foundId == null || foundId == 0){
                Toast.makeText(context, "Please enter valid transfer target", Toast.LENGTH_SHORT).show()
                return
            } else {
                transferId = foundId
            }
        } else {
            category = binding.etCategory.text.toString().trim()
            if (category.isEmpty()){
                Toast.makeText(context, "Please enter category", Toast.LENGTH_SHORT).show()
                return
            }
            transferId = 0
            transferType = null
        }
        // If transfer target is a goal, update its savedAmount after transaction creation

        transaction?.let { trans ->
            if (isNewTrans){
                val newTrans = trans.copy(
                    id = 0, //autogenerated by db
                    type = type,
                    description = description,
                    category = category,
                    amount = amount,
                    date = selectedDate,
                    accountId = currAcc.id,
                    transferToId = transferId,
                    transferToType = transferType
                )
                listener?.onTransactionCreated(newTrans, selectedLoanId)
                Toast.makeText(context, "Transaction created", Toast.LENGTH_SHORT).show()
            }else{
                val updated = trans.copy(
                    type = type,
                    description = description,
                    category = category,
                    amount = amount,
                    date = selectedDate,
                    accountId = currAcc.id,
                    transferToId = transferId,
                    transferToType = transferType
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
