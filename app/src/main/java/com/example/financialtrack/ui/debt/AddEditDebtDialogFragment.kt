package com.example.financialtrack.ui.debt

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.financialtrack.data.model.Debt
import com.example.financialtrack.data.model.DebtType
import com.example.financialtrack.databinding.DialogAddEditDebtBinding
import com.example.financialtrack.utils.FormatUtils
import java.text.SimpleDateFormat
import java.util.*

class AddEditDebtDialogFragment(
    private val debt: Debt?,
    private val onSave: (Debt) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddEditDebtBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddEditDebtBinding.inflate(LayoutInflater.from(context))

        // Populate debt type spinner
        val debtTypes = DebtType.values().map { it.name }
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, debtTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spDebtType.adapter = spinnerAdapter

        // Fill fields if editing
        debt?.let {
            binding.etCreditorName.setText(it.creditorName)
            binding.etAmount.setText(it.amount.toString())
            binding.etAmountPaid.setText(it.amountPaid.toString())
            binding.etDueDate.setText(FormatUtils.formatDate(it.dueDate))
            binding.etInterestRate.setText(it.interestRate.toString())
            binding.spDebtType.setSelection(it.type.ordinal)
        }

        binding.etDueDate.setOnClickListener {
            showDatePicker()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(if (debt == null) "Add Debt/Loan" else "Edit Debt/Loan")
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                val creditorName = binding.etCreditorName.text.toString()
                val amount = binding.etAmount.text.toString().toDoubleOrNull() ?: 0.0
                val amountPaid = binding.etAmountPaid.text.toString().toDoubleOrNull() ?: 0.0
                val dueDate = binding.etDueDate.tag as? Long ?: System.currentTimeMillis()
                val interestRate = binding.etInterestRate.text.toString().toDoubleOrNull() ?: 0.0
                val type = DebtType.values()[binding.spDebtType.selectedItemPosition]

                val newDebt = Debt(
                    id = debt?.id ?: 0,
                    userId = "currentUserId", // replace with actual user ID
                    creditorName = creditorName,
                    amount = amount,
                    amountPaid = amountPaid,
                    dueDate = dueDate,
                    interestRate = interestRate,
                    type = type
                )

                onSave(newDebt)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = android.app.DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val formatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                binding.etDueDate.setText(formatted)
                binding.etDueDate.tag = calendar.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
