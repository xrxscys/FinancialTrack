package com.example.financialtrack.ui.budget

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.financialtrack.databinding.DialogAddEditBudgetBinding
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditBudgetDialogFragment() : DialogFragment(){

    private var _binding : DialogAddEditBudgetBinding? = null

    private var selectedDate: Long = System.currentTimeMillis()
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            //TODO: Implement save logic; will do later
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
}

