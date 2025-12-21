package com.example.financialtrack.ui.reports

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financialtrack.R
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import com.example.financialtrack.databinding.ActivityReportsBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Currency
import java.util.Locale
import kotlin.math.abs

class ReportsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportsBinding
    private val viewModel: ReportsViewModel by viewModels()

    private val symbol= Currency.getInstance(Locale("en", "PH")).symbol

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val user = FirebaseAuth.getInstance().currentUser

        user?.let {
            viewModel.getAllTransactions(it.uid).observe(this) { transactions ->
                updateTotals(transactions)
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun updateTotals(transactions: List<Transaction>) {
        val totalIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val totalExpenses = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        binding.tvTi.text = "$symbol" + formatString(totalIncome)
        binding.tvTe.text = "$symbol" + formatString(totalExpenses)


        if(totalExpenses > totalIncome){
            binding.tvNetIncSymbol.text = "-$symbol"
        } else {
            binding.tvNetIncSymbol.text = "+$symbol"
        }
        val net = totalIncome - totalExpenses
        binding.tvNetInc.text = formatString(abs(net))
    }

    private fun formatString(amount: Double): String {
        return String.format(Locale("en", "PH"), "%.2f", amount)
    }
}
