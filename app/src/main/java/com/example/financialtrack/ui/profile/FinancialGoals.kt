package com.example.financialtrack.ui.profile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financialtrack.R
import com.example.financialtrack.databinding.ActivityFinancialGoalsBinding
import java.util.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.data.model.FinancialGoal
import com.example.financialtrack.ui.profile.FinancialGoalAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import com.example.financialtrack.ui.auth.AuthViewModel
import android.widget.RadioGroup

class FinancialGoals : AppCompatActivity() {
    private lateinit var binding: ActivityFinancialGoalsBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var goalAdapter: FinancialGoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        viewModel.activeGoals.observe(this) { goals ->
            goalAdapter.submitList(goals)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAddGoal.setOnClickListener {
            showAddGoalDialog()
        }
    }

    private fun setupRecyclerView() {
        goalAdapter = FinancialGoalAdapter()

        goalAdapter.onGoalClickListener = { goal ->
            showUpdateAmountDialog(goal)
        }

        binding.rvFinancialGoals.apply {
            adapter = goalAdapter
            layoutManager = LinearLayoutManager(this@FinancialGoals)
        }
    }

    private fun showAddGoalDialog(){
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_goal, null)

        val etGoalName = dialogView.findViewById<EditText>(R.id.et_goal_name)
        val etTargetAmount = dialogView.findViewById<EditText>(R.id.et_target_amount)
        val btnSelectDeadline = dialogView.findViewById<Button>(R.id.btn_select_deadline)

        var selectedDeadline: Long? = null

        btnSelectDeadline.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Deadline")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                selectedDeadline = selectedDate
                val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                btnSelectDeadline.text = sdf.format(Date(selectedDate))
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        builder.setView(dialogView)
            .setTitle("New Financial Goal")
            .setPositiveButton("Add") { dialog, _ ->
                val goalName = etGoalName.text.toString().trim()
                val targetAmount = etTargetAmount.text.toString().toDoubleOrNull()

                if (goalName.isNotEmpty() && targetAmount != null && targetAmount > 0 && selectedDeadline != null) {
                    val userId = authViewModel.currentUser.value?.uid
                    if (userId != null) {
                        val newGoal = FinancialGoal(
                            userId = userId,
                            goalName = goalName,
                            targetAmount = targetAmount,
                            deadline = selectedDeadline!!
                        )
                        viewModel.addGoal(newGoal)
                    } else {
                        Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Please fill all fields correctly!", Toast.LENGTH_SHORT)
                        .show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showUpdateAmountDialog(goal: FinancialGoal){
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_goal_amount, null)

        val etAmountToChange = dialogView.findViewById<EditText>(R.id.et_amount_to_change)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.rg_operation)

        builder.setView(dialogView)
            .setTitle("Update: ${goal.goalName}")
            .setPositiveButton("Save") { dialog, _ ->
                val amountText = etAmountToChange.text.toString().trim()
                if (amountText.isNotEmpty()) {
                    val amount = amountText.toDoubleOrNull()

                    val isAdding = radioGroup.checkedRadioButtonId == R.id.rb_add

                    if (amount != null && amount > 0) {
                        viewModel.updateSavedAMount(goal, amount, isAdding)
                    } else {
                        Toast.makeText(this, "Please enter a valid amount!", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()

    }

}
