package com.example.financialtrack.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.financialtrack.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load DashboardFragment once
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.dashboardContainer.id, DashboardFragment())
                .commit()
        }
    }
}
