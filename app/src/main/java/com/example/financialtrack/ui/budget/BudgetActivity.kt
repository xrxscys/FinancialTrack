package com.example.financialtrack.ui.budget

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.financialtrack.databinding.ActivityBudgetBinding
import com.google.firebase.auth.FirebaseAuth

class BudgetActivity: AppCompatActivity() {

    private lateinit var binding: ActivityBudgetBinding
    private lateinit var adapter: BudgetAdapter

    override fun onCreate(savedInstance: Bundle?){
        super.onCreate(savedInstance)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val auth = FirebaseAuth.getInstance()
//        val user = auth.currentUser


        setupHeader()
    }

    private fun setupHeader(){
        binding.btnBackBudget.setOnClickListener{
            finish()
        }
    }
}
