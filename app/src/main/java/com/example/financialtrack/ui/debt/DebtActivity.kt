package com.example.financialtrack.ui.debt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.data.model.Debt
import com.example.financialtrack.data.model.DebtType
import com.example.financialtrack.databinding.ActivityDebtBinding
import java.text.SimpleDateFormat
import java.util.*

class DebtActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDebtBinding
    private lateinit var debtAdapter: DebtAdapter
    private val debtList = mutableListOf<Debt>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        debtAdapter = DebtAdapter(debtList)
        binding.rvDebts.apply {
            layoutManager = LinearLayoutManager(this@DebtActivity)
            adapter = debtAdapter
        }

        // Back button
        binding.btnBackDebt.setOnClickListener {
            finish()
        }

        // Add sample debt
        addSampleDebt()
    }

    private fun addSampleDebt() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dueDateMillis = sdf.parse("2025-10-31")?.time ?: System.currentTimeMillis()

        debtList.add(
            Debt(
                userId = "user123",
                creditorName = "Creditor A",
                amount = 1000.0,
                amountPaid = 200.0,
                dueDate = dueDateMillis,
                interestRate = 5.0,
                type = DebtType.LOAN,
                description = "Sample debt description"
            )
        )

        debtAdapter.notifyDataSetChanged()
    }
}
