package com.example.financialtrack.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.databinding.FragmentGoalsListBinding
import com.example.financialtrack.data.model.GoalStatus

class CurrentGoalsFragment : Fragment() {
    private var _binding: FragmentGoalsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoalsViewModel by viewModels()
    private lateinit var adapter: GoalsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoalsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = GoalsAdapter()
        binding.rvGoals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGoals.adapter = adapter
        // Observe all goals and update expired ones
        viewModel.goalRepository.getGoalsByUser(viewModel.userId).observe(viewLifecycleOwner) { allGoals ->
            viewModel.updateExpiredGoals(allGoals)
            val activeGoals = allGoals.filter { it.status == com.example.financialtrack.data.model.GoalStatus.ACTIVE }
            adapter.submitList(activeGoals)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    fun showAddGoalDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(com.example.financialtrack.R.layout.dialog_add_goal, null)

        val etGoalName = dialogView.findViewById<android.widget.EditText>(com.example.financialtrack.R.id.et_goal_name)
        val etTargetAmount = dialogView.findViewById<android.widget.EditText>(com.example.financialtrack.R.id.et_target_amount)
        val btnSelectDeadline = dialogView.findViewById<android.widget.Button>(com.example.financialtrack.R.id.btn_select_deadline)

        var selectedDeadline: Long? = null

        btnSelectDeadline.setOnClickListener {
            val datePicker = com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Deadline")
                .setSelection(com.google.android.material.datepicker.MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                selectedDeadline = selectedDate
                val sdf = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault())
                btnSelectDeadline.text = sdf.format(java.util.Date(selectedDate))
            }
            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        builder.setView(dialogView)
            .setTitle("New Goal")
            .setPositiveButton("Add") { dialog, _ ->
                val goalName = etGoalName.text.toString().trim()
                val targetAmount = etTargetAmount.text.toString().toDoubleOrNull()

                if (goalName.isNotEmpty() && targetAmount != null && targetAmount > 0 && selectedDeadline != null) {
                    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        val newGoal = com.example.financialtrack.data.model.FinancialGoal(
                            userId = userId,
                            goalName = goalName,
                            targetAmount = targetAmount,
                            deadline = selectedDeadline!!
                        )
                        viewModel.addGoal(newGoal)
                    } else {
                        android.widget.Toast.makeText(requireContext(), "User not logged in!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                } else {
                    android.widget.Toast.makeText(requireContext(), "Please fill all fields correctly!", android.widget.Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
