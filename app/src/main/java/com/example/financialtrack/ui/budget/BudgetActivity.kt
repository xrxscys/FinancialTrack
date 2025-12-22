package com.example.financialtrack.ui.budget

import android.R
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.financialtrack.data.model.Budget
import com.example.financialtrack.databinding.ActivityBudgetBinding
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseUser

class BudgetActivity: AppCompatActivity(), AddEditBudgetDialogFragment.BudgetDialogListener {

    private lateinit var binding: ActivityBudgetBinding
    private lateinit var adapter: BudgetAdapter
    private val viewModel: BudgetViewModel by viewModels()
    private var userId = ""

    override fun onCreate(savedInstance: Bundle?){
        super.onCreate(savedInstance)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null){
            userId = user.uid
            setupHeader()
            setupCreateButton()
            setupRecyclerView()
            loadBudgetList(user)
        }
    }

    private fun setupRecyclerView() {
        adapter = BudgetAdapter(emptyList())
        adapter.setOnClickListener { budget ->
            //TODO: Implement update logic; will do later
        }
        binding.rvBudgets.layoutManager = LinearLayoutManager(this)
        binding.rvBudgets.adapter = adapter

    }
    private fun loadBudgetList(user: FirebaseUser?){
        viewModel.getAllBudgets(userId).observe(this){
            budgets -> adapter.updateBudgets(budgets)
        }
    }
    private fun setupHeader(){
        binding.btnBackBudget.setOnClickListener{
            finish()
        }
    }

    private fun setupCreateButton(){
        binding.fabAddBudget.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog(){
        val dialog = AddEditBudgetDialogFragment.newInstanceCreate(userId)
        dialog.setListener(this)
        dialog.show(supportFragmentManager, "AddEditBudgetDialog")
    }

    override fun onBudgetUpdate(budget: Budget){
        viewModel.insertBudget(budget)
        Log.d("BudgetActivity", "Budget updated: $budget")
    }

    override fun onBudgetDelete(budget: Budget){
        //TODO: Implement update logic; will do later
        viewModel.deleteBudget(budget)
    }
}
