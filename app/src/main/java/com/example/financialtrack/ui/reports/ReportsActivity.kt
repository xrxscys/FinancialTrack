package com.example.financialtrack.ui.reports

import java.util.Calendar
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financialtrack.R
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import com.example.financialtrack.databinding.ActivityReportsBinding
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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
                showBarChart(transactions)
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun updateTotals(transactions: List<Transaction>) {
        var totalIncome = 0.0
        var totalExpenses = 0.0

        for (t in transactions) {
            when (t.type) {
                TransactionType.INCOME -> totalIncome += t.amount
                TransactionType.EXPENSE -> totalExpenses += t.amount
                else -> {}
            }
        }

        binding.tvTi.text = "$symbol${formatString(totalIncome)}"
        binding.tvTe.text = "$symbol${formatString(totalExpenses)}"

        if(totalExpenses > totalIncome){
            binding.tvNetIncSymbol.text = "-$symbol"
        } else {
            binding.tvNetIncSymbol.text = "+$symbol"
        }
        val net = totalIncome - totalExpenses
        binding.tvNetInc.text = formatString(abs(net))
    }

    private fun showBarChart(transactions: List<Transaction>) {
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")

        val totalsByDay = FloatArray(7) { 0f }
        val cal = Calendar.getInstance()
        transactions.forEach { tx ->
            cal.timeInMillis = tx.date
            val dayIndex = cal.get(Calendar.DAY_OF_WEEK) - 1
            if (dayIndex in 0..6) {
                totalsByDay[dayIndex] += tx.amount.toFloat()
            }
        }

        val todayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1

        val entries = totalsByDay.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value)
        }

        binding.barChart.renderer = RoundedBarRenderer(
            binding.barChart,
            binding.barChart.animator,
            binding.barChart.viewPortHandler,
            radius = 12f
        )

        val dataSet = BarDataSet(entries, "Weekly Spending").apply {
            color = ContextCompat.getColor(this@ReportsActivity, R.color.primary)
            valueTextSize = 0f
            barBorderWidth = 0f
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.85f
        }

        binding.barChart.apply {
            data = barData
            setFitBars(true)
            description.isEnabled = false
            animateY(600)
            legend.isEnabled = false

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(daysOfWeek)
                granularity = 1f
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }

            axisLeft.apply {
                axisMinimum = 0f
                val maxVal = totalsByDay.maxOrNull() ?: 0f
                val interval = when {
                    maxVal >= 1000 -> 1000f
                    maxVal >= 100 -> 100f
                    else -> 10f
                }
                granularity = interval
                setDrawGridLines(false)
                setDrawAxisLine(false)

                val centerValue = (maxVal / 2f)
                val centerLine = LimitLine(centerValue)
                centerLine.lineWidth = 2f
                centerLine.lineColor = ContextCompat.getColor(context, R.color.gray)
                centerLine.enableDashedLine(10f, 10f, 0f)
                centerLine.textSize = 12f

                addLimitLine(centerLine)
            }

            axisRight.isEnabled = false
            invalidate()
        }

    }

    private fun formatString(amount: Double): String {
        return String.format(Locale("en", "PH"), "%.2f", amount)
    }
}
