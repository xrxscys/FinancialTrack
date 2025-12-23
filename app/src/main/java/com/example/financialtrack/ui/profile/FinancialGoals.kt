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
class FinancialGoals : AppCompatActivity() {
    private lateinit var binding: ActivityFinancialGoalsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up ViewPager2 and TabLayoutMediator
        val pagerAdapter = com.example.financialtrack.ui.goals.GoalsPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        com.google.android.material.tabs.TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            tab.text = if (position == 0) "Current" else "History"
        }.attach()

        binding.btnBack.setOnClickListener {
            finish()
        }

        // Use the add button in the activity to show the add goal dialog in the fragment
        binding.btnAddGoal.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentByTag("f0") as? com.example.financialtrack.ui.goals.CurrentGoalsFragment
            fragment?.showAddGoalDialog()
        }
    }
}
