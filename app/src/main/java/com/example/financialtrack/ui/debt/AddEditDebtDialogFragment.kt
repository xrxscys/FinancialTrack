package com.example.financialtrack.ui.debt

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
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
    private val userId: String,
    private val debt: Debt?,
    private val onSave: (Debt) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddEditDebtBinding? = null
    private val binding get() = _binding!!
    private var selectedDateMillis: Long = System.currentTimeMillis()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddEditDebtBinding.inflate(LayoutInflater.from(context))

        if (debt != null) {
            binding.etCreditorName.setText(debt.creditorName)
            binding.etAmount.setText(debt.amount.toString())
            binding.etDescription.setText(debt.description)
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.etDueDate.setText(sdf.format(Date(debt.dueDate)))
            selectedDateMillis = debt.dueDate
        } else {
            selectedDateMillis = System.currentTimeMillis()
        }

        binding.etDueDate.setOnClickListener {
            showDatePicker()
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

        // Set the time to 10:00 AM on the selected date
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDateMillis
        calendar.set(Calendar.HOUR_OF_DAY, 10)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val dueDateWithTime = calendar.timeInMillis

        val debtObj = if (debt != null) {
            debt.copy(
                creditorName = creditor,
                amount = amount,
                description = description,
                dueDate = dueDateWithTime,
                isActive = true,
                paidAt = null,
                amountPaid = 0.0,
                remainingBalance = amount
            )
        } else {
            Debt(
                id = 0,
                userId = userId,
                creditorName = creditor,
                amount = amount,
                amountPaid = 0.0,
                remainingBalance = amount,
                description = description,
                dueDate = dueDateWithTime,
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
        calendar.timeInMillis = selectedDateMillis

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDateMillis = calendar.timeInMillis

                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                binding.etDueDate.setText(sdf.format(Date(selectedDateMillis)))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

