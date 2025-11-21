package com.example.financialtrack.ui.reports

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.financialtrack.R
import com.example.financialtrack.databinding.ActivityNotificationBinding
import com.example.financialtrack.databinding.ActivityReportsBinding

class ReportsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackReports.setOnClickListener {
            finish()
        }
    }
}
