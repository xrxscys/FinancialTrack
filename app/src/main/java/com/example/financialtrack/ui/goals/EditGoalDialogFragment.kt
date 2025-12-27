package com.example.financialtrack.ui.goals

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.financialtrack.data.model.FinancialGoal
import com.example.financialtrack.databinding.DialogEditGoalBinding

class EditGoalDialogFragment : DialogFragment() {
        
    private var selectedDeadline: Long? = null
    private var _binding: DialogEditGoalBinding? = null
    private val binding get() = _binding!!
    private val goalsViewModel: GoalsViewModel by viewModels()

    private var goalId: Int = -1

    override fun onStart() {
            super.onStart()
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

    companion object {
        private const val ARG_GOAL_ID = "goal_id"
        fun newInstance(goalId: Int): EditGoalDialogFragment {
            val fragment = EditGoalDialogFragment()
            val args = Bundle()
            args.putInt(ARG_GOAL_ID, goalId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goalId = arguments?.getInt(ARG_GOAL_ID, -1) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DialogEditGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe and populate goal fields
        goalsViewModel.goalRepository.getGoalsByUser(goalsViewModel.userId).observe(viewLifecycleOwner) { allGoals ->
            val goal = allGoals.find { it.id == goalId } ?: return@observe
            binding.etGoalName.setText(goal.goalName)
            binding.etTargetAmount.setText(goal.targetAmount.toString())
            selectedDeadline = goal.deadline
            val dateFormatter = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault())
            binding.btnSelectDeadline.text = dateFormatter.format(java.util.Date(goal.deadline))

            binding.btnSelectDeadline.setOnClickListener {
                val calendar = java.util.Calendar.getInstance()
                calendar.timeInMillis = selectedDeadline ?: goal.deadline
                val minDate = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, 1) }.timeInMillis
                val datePicker = android.app.DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        selectedDeadline = calendar.timeInMillis
                        binding.btnSelectDeadline.text = dateFormatter.format(java.util.Date(selectedDeadline!!))
                    },
                    calendar.get(java.util.Calendar.YEAR),
                    calendar.get(java.util.Calendar.MONTH),
                    calendar.get(java.util.Calendar.DAY_OF_MONTH)
                )
                datePicker.datePicker.minDate = minDate
                datePicker.show()
            }

            binding.btnSave.setOnClickListener {
                val name = binding.etGoalName.text.toString().trim()
                val targetAmount = binding.etTargetAmount.text.toString().toDoubleOrNull()
                val deadline = selectedDeadline ?: goal.deadline
                val now = System.currentTimeMillis()
                if (name.isEmpty() || targetAmount == null || targetAmount <= 0) {
                    Toast.makeText(requireContext(), "Please enter valid data", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (deadline <= now) {
                    Toast.makeText(requireContext(), "Deadline must be after today", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val updatedGoal = goal.copy(goalName = name, targetAmount = targetAmount, deadline = deadline)
                goalsViewModel.archiveGoal(updatedGoal) // Use your update method
                Toast.makeText(requireContext(), "Goal updated", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
        binding.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
