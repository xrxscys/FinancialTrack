package com.example.financialtrack.ui.budget

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.BaseBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.financialtrack.data.model.Budget
import com.example.financialtrack.databinding.DialogAddEditBudgetBinding
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.financialtrack.data.model.BudgetPeriod
import com.google.firebase.auth.FirebaseUser

class AddEditBudgetDialogFragment() : DialogFragment(){

    private var _binding : DialogAddEditBudgetBinding? = null

    private var selectedDate: Long = System.currentTimeMillis()
    private var endDate: Long = System.currentTimeMillis()
    private val binding get() = _binding!!

    val dateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())


    private var budget: Budget? = null

    private var listener : BudgetDialogListener ? = null
    private var isNewBudget = false
    interface BudgetDialogListener{
        fun onBudgetUpdate(budget: Budget)
        fun onBudgetDelete(budget: Budget)
        fun onBudgetCreate(budget:Budget)

    }

    companion object{
        private const val ARG_ID = "id"
        private const val ARG_USER_ID = "userId"
        private const val ARG_CATEGORY = "category"
        private const val ARG_AMOUNT_LIMIT ="amountLimit"
        private const val ARG_DATE_START = "dateStart"
        private const val ARG_DATE_END = "dateEnd"
        private const val ARG_PERIOD = "period"
        private const val ARG_IS_NEW = "isNew"
        fun newInstanceCreate(userId: String): AddEditBudgetDialogFragment {
            val fragment = AddEditBudgetDialogFragment()
            val args = Bundle()

            args.putString(ARG_USER_ID, userId)
            args.putBoolean(ARG_IS_NEW, true)
            fragment.arguments = args
            return fragment
        }

        fun newInstanceEdit(budget: Budget): AddEditBudgetDialogFragment {
            val fragment = AddEditBudgetDialogFragment()
            val args = Bundle()

            args.putLong(ARG_ID, budget.id)
            args.putString(ARG_USER_ID, budget.userId)
            args.putString(ARG_CATEGORY, budget.category)
            args.putDouble(ARG_AMOUNT_LIMIT, budget.amount)
            args.putString(ARG_PERIOD, budget.period.name)
            args.putLong(ARG_DATE_START, budget.startDate)
            args.putLong(ARG_DATE_END, budget.endDate)

            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { args ->
            isNewBudget = args.getBoolean(ARG_IS_NEW, false)

            if (isNewBudget) {
                val userId = args.getString(ARG_USER_ID)?: ""

                budget = Budget(
                    id = 0,
                    userId = userId,
                    category = "",
                    amount = 0.0,
                    period = BudgetPeriod.MONTHLY,
                    startDate = System.currentTimeMillis(),
                    endDate = System.currentTimeMillis()
                )
            }else{

                val id = args.getLong(ARG_ID)
                val userId = args.getString(ARG_USER_ID) ?: ""
                val category = args.getString(ARG_CATEGORY) ?: ""
                val amount = args.getDouble(ARG_AMOUNT_LIMIT, 0.0) // Note: Check if ARG_AMOUNT_LIMIT is used correctly
                val period = BudgetPeriod.valueOf(args.getString(ARG_PERIOD) ?: "MONTHLY")
                val startDate = args.getLong(ARG_DATE_START)
                endDate = args.getLong(ARG_DATE_END)
                budget = Budget(
                    id = id,
                    userId = userId,
                    category = category,
                    amount = amount, // Note: Check if ARG_AMOUNT_LIMIT is used correctly
                    period = period,
                    startDate = startDate,
                    endDate = endDate
                )

                // Sync local variables with the existing budget data
                selectedDate = budget!!.startDate
                endDate = budget!!.endDate
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogAddEditBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
        setupPeriodDropdown()
        setupDatePicker()
        setupClickListeners()
        populateFields()
    }

    private fun populateFields(){
        budget?.let {bud->
            if(!isNewBudget){
                binding.etCategory.setText(bud.category)
                binding.etBudgetLimit.setText(bud.amount.toString())
                binding.actvPeriod.setText(when(bud.period.name){
                    "DAILY" -> "Daily"
                    "WEEKLY" -> "Weekly"
                    "MONTHLY" -> "Monthly"
                    else -> "Yearly"
                }, false)
                binding.etStartDate.setText(dateFormatter.format(bud.startDate))
                binding.etEndDate.setText(dateFormatter.format(bud.endDate))
            }else{
                binding.etStartDate.setText(dateFormatter.format(bud.startDate))
            }
        }
    }
    private fun setupDialog(){
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupPeriodDropdown(){
        val periods = arrayOf("Daily", "Weekly", "Monthly", "Yearly")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, periods)
        binding.actvPeriod.setAdapter(adapter)

        binding.actvPeriod.setOnItemClickListener { _, _, position, _ ->
            val selectedPeriod = when (periods[position]) {
                "Daily" -> BudgetPeriod.DAILY
                "Weekly" -> BudgetPeriod.WEEKLY
                "Monthly" -> BudgetPeriod.MONTHLY
                else -> BudgetPeriod.YEARLY
            }
            endDate = calculateEndDate(selectedPeriod)
            updateDatePicker()
        }
    }

    private fun setupClickListeners(){
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveBudget()
        }

        binding.btnDelete.setOnClickListener{
            deleteBudget()
        }
    }


    private fun setupDatePicker(){
        updateDatePicker()

        binding.etStartDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun updateDatePicker(){
        val periodString = binding.actvPeriod.text.toString()
        val periodSet = when (periodString){
            "Daily" -> BudgetPeriod.DAILY
            "Weekly" -> BudgetPeriod.WEEKLY
            "Monthly" -> BudgetPeriod.MONTHLY
            "Yearly" -> BudgetPeriod.YEARLY
            else -> budget?.period ?: BudgetPeriod.DAILY
        }

        endDate = calculateEndDate(periodSet)

        val start = Date(selectedDate)
        val end = Date(endDate)
        binding.etStartDate.setText(dateFormatter.format(start))
        binding.etEndDate.setText(dateFormatter.format(end))
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

    private fun calculateEndDate(period: BudgetPeriod): Long{
        val endDateCalc = Calendar.getInstance()
        endDateCalc.timeInMillis = selectedDate
        when (period){
            BudgetPeriod.DAILY -> {
                endDateCalc.add(Calendar.DATE, 1)
            }
            BudgetPeriod.MONTHLY -> {
                endDateCalc.add(Calendar.MONTH, 1)
            }
            BudgetPeriod.WEEKLY -> {
                endDateCalc.add(Calendar.WEEK_OF_MONTH, 1)
            }
            else -> {
                endDateCalc.add(Calendar.YEAR, 1)
            }
        }
        val returnDate = endDateCalc.timeInMillis - 1
        return returnDate
    }
    private fun saveBudget(){
        val periodString = binding.actvPeriod.text.toString()
        val periodSet = when(periodString){
            "Daily" -> BudgetPeriod.DAILY
            "Weekly" -> BudgetPeriod.WEEKLY
            "Monthly" -> BudgetPeriod.MONTHLY
            else -> BudgetPeriod.YEARLY
        }
        val id = budget?.id ?: 0
        val category = binding.etCategory.text.toString().trim()
        val budgetLimit = binding.etBudgetLimit.text.toString().trim()
        val finalStartDate = selectedDate
        val finalEndDate = endDate

        budget?.let { bud ->
            val newBud = bud.copy(
                id = id,
                category = category,
                amount = budgetLimit.toDouble(),
                period = periodSet,
                startDate = finalStartDate,
                endDate = finalEndDate
            )
            if (!isNewBudget){
                listener?.onBudgetUpdate(newBud)
            }else{
                listener?.onBudgetCreate(newBud)
            }

        }
        dismiss()
    }

    private fun deleteBudget() {
        budget?.let { bud ->
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Budget")
                .setMessage("Are you sure you want to delete this budget?")
                .setPositiveButton("Delete") { _, _ ->
                    listener?.onBudgetDelete(bud)
                    Toast.makeText(context, "Budget deleted", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
    fun setListener(listener: BudgetDialogListener){
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

