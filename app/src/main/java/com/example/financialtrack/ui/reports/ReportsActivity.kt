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
import com.example.financialtrack.data.model.Debt
import com.example.financialtrack.data.model.FinancialGoal
import com.example.financialtrack.data.model.GoalStatus
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import com.example.financialtrack.databinding.ActivityReportsBinding
import com.example.financialtrack.ui.debt.DebtViewModel
import com.example.financialtrack.ui.goals.GoalsViewModel
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.abs

data class TimeRemaining(
    val dueDate: String,
    val daysRemaining: Int,
    val daysRemainingFormatted: String,
    val weeksRemaining: Double
)

class ReportsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportsBinding
    private val viewModel: ReportsViewModel by viewModels()
    private val debtViewModel: DebtViewModel by viewModels()
    private val goalViewModel: GoalsViewModel by viewModels()

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

        user?.let { firebaseUser ->
            viewModel.getAllTransactions(firebaseUser.uid).observe(this) { transactions ->
                updateOverview(transactions)
                showBarChart(transactions)
            }

            debtViewModel.getActiveDebts(firebaseUser.uid)

           debtViewModel.activeDebts.observe(this) { debts ->
               updateDebts(debts)
           }

            goalViewModel.getGoalsByStatus(GoalStatus.ACTIVE).observe(this){ goals ->
                if (goals.isEmpty()) {
                    updateGoals(emptyList())
                    return@observe
                }

                val populatedGoals = mutableListOf<FinancialGoal>()
                var loadedCount = 0

                goals.forEach { goal ->
                    goalViewModel.getSavedAmountForGoal(goal.id).observe(this) { saved ->
                        val updatedGoal = goal.copy(savedAmount = saved ?: 0.0)
                        populatedGoals.add(updatedGoal)
                        loadedCount++

                        if (loadedCount == goals.size) {
                            updateGoals(populatedGoals)
                        }
                    }
                }
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun updateOverview(transactions: List<Transaction>) {
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
    private fun updateDebts(debts: List<Debt>) {
        if (debts.isEmpty()) {
            binding.tvBalanceValue.text = "${symbol}0.00"
            binding.tvNextPaymentValue.text = "No payment due"
            return
        }

        val totalDebt = debts.sumOf { it.amount }
        val totalPaid = debts.sumOf { it.amountPaid }


        val nextDebt = debts.filter { it.amountPaid < it.amount }
            .minByOrNull { it.dueDate }

        nextDebt?.let { debt ->
           val dates = getDaysWeeksRemaining(debt.dueDate)
            val dueDateFormatted= dates.dueDate
            val daysRemainingFormatted = dates.daysRemainingFormatted

            binding.tvNextPaymentValue.text = "$dueDateFormatted, $daysRemainingFormatted"
        }

        val progress = calculatePercentage(totalDebt, totalPaid)

        binding.tvBalanceValue.text = "$symbol${formatString(abs(totalDebt - totalPaid))}"
        binding.progressDebtsLoans.setProgressCompat(progress, true)
        binding.tvDebtPercent.text = String.format("%d%%", progress)
    }

    private fun updateGoals(goals: List<FinancialGoal>) {
        if (goals.isEmpty()) {
            binding.tvTotalSavedValue.text = "${symbol}0.00"
            binding.tvRemainingValue.text = "${symbol}0.00"
            return
        }

        val totalSaved = goals.sumOf { it.savedAmount }
        val remaining = goals.sumOf { it.targetAmount - it.savedAmount }
        val targetAmount = goals.sumOf { it.targetAmount }

        val nearestGoal = goals.filter { it.status == GoalStatus.ACTIVE }
            .minByOrNull { it.deadline}

        nearestGoal?.let { goal ->
            val dates = getDaysWeeksRemaining(goal.deadline)
            val daysRemaining = dates.daysRemaining
            val daysRemainingFormatted = dates.daysRemainingFormatted
            val dueDateFormatted = dates.dueDate
            val weeksRemaining = dates.weeksRemaining

            binding.tvAvgPerTimepDailyValue.text = "Daily: $symbol${formatString(goal.targetAmount / daysRemaining)}"
            binding.tvAvgPerTimepWeeklyValue.text = "Weekly: $symbol${formatString(goal.targetAmount / weeksRemaining)}"

            binding.tvNearestGoalValue.text = "$symbol${formatString(goal.targetAmount)}"
            binding.tvNearestGoalDeadlineValue.text = "$daysRemainingFormatted ($dueDateFormatted)"
        }

        val progress = calculatePercentage(targetAmount, totalSaved)

        binding.tvTotalSavedValue.text = "$symbol$totalSaved"
        binding.tvRemainingValue.text = "$symbol$remaining"

        binding.progressGoals.setProgressCompat(progress, true)
        binding.tvProgressGoalsPercent.text = String.format("%d%%", progress)
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

        val entries = totalsByDay.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value)
        }

        val dataSet = BarDataSet(entries, "Weekly Spending").apply {
            color = ContextCompat.getColor(this@ReportsActivity, R.color.primary)
            valueTextSize = 0f
            barBorderWidth = 0f
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.5f
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

    private fun calculatePercentage(total: Double, amount: Double): Int {
        val progress = ((amount / total) * 100).toInt()
        return if (progress > 100) { 100 } else progress
    }

    private fun getDaysWeeksRemaining(date: Long): TimeRemaining{
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dueDateFormatted = dateFormatter.format(date)

        val diffMillis = date - System.currentTimeMillis()
        val daysRemaining = (diffMillis / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)

        val daysRemainingFormatted = when (daysRemaining) {
            0 -> {
                "Today"
            }
            1 -> {
                "Tomorrow"
            }
            else -> {
                "In $daysRemaining days"
            }
        }

        val weeksRemaining = (daysRemaining / 7.0).coerceAtLeast(1.0)
        return TimeRemaining(dueDateFormatted, daysRemaining,daysRemainingFormatted, weeksRemaining)
    }

    private fun formatString(amount: Double): String {
        return String.format(Locale("en", "PH"), "%.2f", amount)
    }
}
