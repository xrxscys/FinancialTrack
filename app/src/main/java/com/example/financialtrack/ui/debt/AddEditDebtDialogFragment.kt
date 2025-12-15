package com.example.financialtrack.ui.debt

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.financialtrack.data.model.Debt
import com.example.financialtrack.data.model.DebtType
import com.example.financialtrack.databinding.DialogAddEditDebtBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditDebtDialogFragment(
    private val debt: Debt?,
    private val onSave: (Debt) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddEditDebtBinding? = null
    private val binding get() = _binding!!
    private var selectedDateTimeMillis: Long = System.currentTimeMillis()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddEditDebtBinding.inflate(LayoutInflater.from(context))

        if (debt != null) {
            binding.etCreditorName.setText(debt.creditorName)
            binding.etAmount.setText(debt.amount.toString())
            binding.etDescription.setText(debt.description)
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.etDueDate.setText(sdf.format(Date(debt.dueDate)))
            selectedDateTimeMillis = debt.dueDate
            val timeSdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            binding.etDueTime.setText(timeSdf.format(Date(debt.dueDate)))
        } else {
            selectedDateTimeMillis = System.currentTimeMillis()
        }

        binding.etDueDate.setOnClickListener {
            showDatePicker()
        }

        binding.etDueTime.setOnClickListener {
            showTimePicker()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(if (debt == null) "Add Loan" else "Edit Loan")
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                if (validateInputs()) {
                    saveDebt()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun validateInputs(): Boolean {
        val creditor = binding.etCreditorName.text.toString().trim()
        val amount = binding.etAmount.text.toString().trim()
        val dueDate = binding.etDueDate.text.toString().trim()

        if (creditor.isEmpty()) {
            Toast.makeText(context, "Please enter loan title", Toast.LENGTH_SHORT).show()
            return false
        }

        if (amount.isEmpty() || amount.toDoubleOrNull() == null || amount.toDouble() <= 0) {
            Toast.makeText(context, "Please enter valid amount in Philippine Pesos", Toast.LENGTH_SHORT).show()
            return false
        }

        if (dueDate.isEmpty()) {
            Toast.makeText(context, "Please select due date", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveDebt() {
        val creditor = binding.etCreditorName.text.toString().trim()
        val amount = binding.etAmount.text.toString().toDouble()
        val description = binding.etDescription.text.toString().trim()

        val debtObj = if (debt != null) {
            debt.copy(
                creditorName = creditor,
                amount = amount,
                description = description,
                dueDate = selectedDateTimeMillis,
                isActive = true,
                paidAt = null
            )
        } else {
            Debt(
                id = 0,
                userId = "user123",
                creditorName = creditor,
                amount = amount,
                description = description,
                dueDate = selectedDateTimeMillis,
                type = DebtType.LOAN,
                isActive = true,
                createdAt = System.currentTimeMillis(),
                paidAt = null
            )
        }

        onSave(debtObj)
        dismiss()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDateTimeMillis

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDateTimeMillis = calendar.timeInMillis

                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                binding.etDueDate.setText(sdf.format(Date(selectedDateTimeMillis)))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDateTimeMillis

        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                selectedDateTimeMillis = calendar.timeInMillis

                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                binding.etDueTime.setText(sdf.format(Date(selectedDateTimeMillis)))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

