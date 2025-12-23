package com.example.financialtrack.ui.budget

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.BaseBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
    private val binding get() = _binding!!

    private var budget: Budget? = null

    private var listener : BudgetDialogListener ? = null
    private var isNewBudget = false
    interface BudgetDialogListener{
        fun onBudgetUpdate(budget: Budget)
        fun onBudgetDelete(budget: Budget)

    }

    companion object{
        private const val ARG_ID = "id"
        private const val ARG_USER_ID = "userId"
        private const val ARG_CATEGORY = "category"
        private const val ARG_AMOUNT_LIMIT ="amountLimit"
        private const val ARG_DATE = "date"
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
                    period = BudgetPeriod.DAILY,
                    startDate = System.currentTimeMillis(),
                    endDate = System.currentTimeMillis()
                )
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
        setupClickListeners()
        setupDatePicker()
        setupClickListeners()
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
    }

    private fun setupClickListeners(){
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveBudget()
        }
    }

    private fun setupDatePicker(){
        updateDatePicker()

        binding.etStartDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun updateDatePicker(){
        val date = Date(selectedDate)
        val dateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        binding.etStartDate.setText(dateFormatter.format(date))
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

    private fun saveBudget(){
        val periodString = binding.actvPeriod.text.toString()
        val periodSet = when(periodString){
            "Daily" -> BudgetPeriod.DAILY
            "Weekly" -> BudgetPeriod.WEEKLY
            "Monthly" -> BudgetPeriod.MONTHLY
            else -> BudgetPeriod.YEARLY
        }

        val category = binding.etCategory.text.toString().trim()
        val budgetLimit = binding.etBudgetLimit.text.toString().trim()
        val startDate = binding.etStartDate.text.toString().trim()
        val endDateCalc = Calendar.getInstance()
        endDateCalc.timeInMillis = selectedDate

        when (periodSet){
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
        val endDate = endDateCalc.timeInMillis - 1

        budget?.let { bud ->
            val newBud = bud.copy(
                id = 0,
                category = category,
                amount = budgetLimit.toDouble(),
                period = periodSet,
                startDate = selectedDate,
                endDate = endDate
            )
            listener?.onBudgetUpdate(newBud)

        }
        dismiss()


    }
    fun setListener(listener: BudgetDialogListener){
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

