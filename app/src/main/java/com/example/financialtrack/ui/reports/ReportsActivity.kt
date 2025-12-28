package com.example.financialtrack.ui.reports

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financialtrack.R
import com.example.financialtrack.data.model.*
import com.example.financialtrack.databinding.ActivityReportsBinding
import com.example.financialtrack.ui.accounts.AccountsViewModel
import com.example.financialtrack.ui.debt.DebtViewModel
import com.example.financialtrack.ui.goals.GoalsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class ReportsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportsBinding
    private val viewModel: ReportsViewModel by viewModels()
    private val debtViewModel: DebtViewModel by viewModels()
    private val goalViewModel: GoalsViewModel by viewModels()
    private val accountsViewModel: AccountsViewModel by viewModels()

    private val symbol = Currency.getInstance(Locale("en", "PH")).symbol
    private var selectedAccountId: Int? = null
    private var allTransactions: List<Transaction> = emptyList()

    private enum class StatsRange { WEEKLY, MONTHLY, RANGE }

    private var selectedRange: StatsRange = StatsRange.MONTHLY
    private var rangeStart: Long = 0
    private var rangeEnd: Long = 0

    private data class TimeRemaining(
        val dueDate: String,
        val daysRemaining: Int,
        val daysRemainingFormatted: String,
        val weeksRemaining: Double
    )

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

        setInitialMonth()
        setupAccountFilter()
        observeTransactions()
        observeDebts()
        observeGoals()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnStatsRange.setOnClickListener { showStatsRangeDialog() }
    }

    private fun setInitialMonth() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        rangeStart = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        rangeEnd = cal.timeInMillis
        selectedRange = StatsRange.MONTHLY

        val now = Calendar.getInstance()
        binding.btnStatsRange.text = if (
            cal.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
            cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
        ) "This Month" else SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(
            Date(
                rangeStart
            )
        )

        updateDateRangeLabel()
    }

    private fun setupAccountFilter() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        accountsViewModel.accounts.observe(this) { accounts ->
            val accountNames = mutableListOf("All Accounts") + accounts.map { it.name }
            binding.btnAccountFilter.text = "All Accounts"

            binding.btnAccountFilter.setOnClickListener {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Select Account")
                    .setItems(accountNames.toTypedArray()) { _, index ->
                        selectedAccountId = if (index == 0) null else accounts[index - 1].id
                        binding.btnAccountFilter.text = accountNames[index]
                        updateAll()
                    }
                    .show()
            }
        }
    }

    private fun observeTransactions() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        viewModel.getAllTransactions(user.uid).observe(this) {
            allTransactions = it
            updateAll()
        }
    }

    private fun observeDebts() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        debtViewModel.getActiveDebts(user.uid)
        debtViewModel.activeDebts.observe(this) { updateDebts(it) }
    }

    private fun observeGoals() {
        goalViewModel.getGoalsByStatus(GoalStatus.ACTIVE).observe(this) { goals ->
            if (goals.isEmpty()) updateGoals(emptyList()) else populateGoals(goals)
        }
    }

    private fun populateGoals(goals: List<FinancialGoal>) {
        val populatedGoals = mutableListOf<FinancialGoal>()
        var loadedCount = 0
        goals.forEach { goal ->
            goalViewModel.getSavedAmountForGoal(goal.id).observe(this) { saved ->
                populatedGoals.add(goal.copy(savedAmount = saved ?: 0.0))
                loadedCount++
                if (loadedCount == goals.size) updateGoals(populatedGoals)
            }
        }
    }

    private fun showStatsRangeDialog() {
        val options = arrayOf("Weekly", "Monthly", "Custom Range")
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Stats Range")
            .setItems(options) { _, index ->
                when (index) {
                    0 -> showWeeklyDialog()
                    1 -> showMonthlyDialog()
                    2 -> showCustomRangeDialog()
                }
            }
            .show()
    }

    private fun showWeeklyDialog() {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val weeks = mutableListOf<Pair<Long, Long>>()
        val labels = mutableListOf<String>()
        var day = 1

        while (day <= maxDay) {
            val start = Calendar.getInstance().apply { set(year, month, day, 0, 0, 0) }
            val endDay = (day + 6).coerceAtMost(maxDay)
            val end = Calendar.getInstance().apply { set(year, month, endDay, 23, 59, 59) }
            weeks.add(start.timeInMillis to end.timeInMillis)
            labels.add("${formatDate(start.timeInMillis)} - ${formatDate(end.timeInMillis)}")
            day += 7
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Select Week")
            .setItems(labels.toTypedArray()) { _, index ->
                rangeStart = weeks[index].first
                rangeEnd = weeks[index].second
                selectedRange = StatsRange.WEEKLY
                binding.btnStatsRange.text = "Week ${index + 1}"
                updateDateRangeLabel()
                updateAll()
            }
            .show()
    }

    private fun showMonthlyDialog() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear - 5..currentYear + 5).map { it.toString() }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("Select Year")
            .setItems(years) { _, yearIndex ->
                val selectedYear = currentYear - 5 + yearIndex
                val months = arrayOf(
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
                )
                MaterialAlertDialogBuilder(this)
                    .setTitle("Select Month")
                    .setItems(months) { _, monthIndex ->
                        val cal = Calendar.getInstance().apply {
                            set(selectedYear, monthIndex, 1, 0, 0, 0)
                            rangeStart = timeInMillis
                            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                            rangeEnd = timeInMillis
                        }
                        selectedRange = StatsRange.MONTHLY
                        binding.btnStatsRange.text =
                            if (monthIndex == Calendar.getInstance().get(Calendar.MONTH) &&
                                selectedYear == Calendar.getInstance().get(Calendar.YEAR)
                            ) "This Month"
                            else SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(
                                Date(
                                    rangeStart
                                )
                            )
                        updateDateRangeLabel()
                        updateAll()
                    }
                    .show()
            }
            .show()
    }

    private fun showCustomRangeDialog() {
        val selection = androidx.core.util.Pair(rangeStart, rangeEnd)
        MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Date Range")
            .setSelection(selection)
            .build()
            .apply {
                addOnPositiveButtonClickListener { range ->
                    rangeStart = range.first
                    rangeEnd = range.second
                    selectedRange = StatsRange.RANGE
                    binding.btnStatsRange.text =
                        "${formatDate(rangeStart)} - ${formatDate(rangeEnd)}"
                    updateDateRangeLabel()
                    updateAll()
                }
            }
            .show(supportFragmentManager, "DATE_RANGE_PICKER")
    }

    private fun updateDateRangeLabel() {
        binding.tvDateRange.text = "${formatDate(rangeStart)} - ${formatDate(rangeEnd)}"
    }

    private fun updateAll() {
        val filtered = selectedAccountId?.let { accountId ->
            allTransactions.filter { it.accountId == accountId || it.transferToId == accountId }
        } ?: allTransactions
        val dateFiltered = filtered.filter { it.date in rangeStart..rangeEnd }
        updateOverview(dateFiltered)
        showBarChart(dateFiltered)
        showPieChart(dateFiltered)
    }

    private fun updateOverview(transactions: List<Transaction>) {
        var totalIncome = 0.0
        var totalExpenses = 0.0
        transactions.forEach { t ->
            when (t.type) {
                TransactionType.INCOME -> totalIncome += t.amount
                TransactionType.EXPENSE -> totalExpenses += t.amount
                else -> {}
            }
        }
        binding.tvTi.text = "$symbol${formatAmount(totalIncome)}"
        binding.tvTe.text = "$symbol${formatAmount(totalExpenses)}"
        binding.tvNetIncSymbol.text = if (totalExpenses > totalIncome) "-$symbol" else "+$symbol"
        binding.tvNetInc.text = formatAmount(abs(totalIncome - totalExpenses))
        binding.tvSpentSymbol.text = "$symbol"

        val days = ((rangeEnd - rangeStart) / (1000 * 60 * 60 * 24)).toInt() + 1
        val perDay = if (days > 0) totalExpenses / days else 0.0
        binding.tvSpent.text = formatAmount(perDay)
    }

    private fun updateDebts(debts: List<Debt>) {
        if (debts.isEmpty()) {
            binding.tvBalanceValue.text = "${symbol}0.00"
            binding.tvNextPaymentValue.text = "No payment due"
            return
        }
        val totalDebt = debts.sumOf { it.amount }
        val totalPaid = debts.sumOf { it.amountPaid }
        val nextDebt = debts.filter { it.amountPaid < it.amount }.minByOrNull { it.dueDate }
        nextDebt?.let {
            val rem = getDaysWeeksRemaining(it.dueDate)
            binding.tvNextPaymentValue.text = "${rem.dueDate}, ${rem.daysRemainingFormatted}"
        }
        val progress = ((totalPaid / totalDebt) * 100).toInt().coerceAtMost(100)
        binding.tvBalanceValue.text = "$symbol${formatAmount(totalDebt - totalPaid)}"
        binding.progressDebtsLoans.setProgressCompat(progress, true)
        binding.tvDebtPercent.text = "$progress%"
    }

    private fun updateGoals(goals: List<FinancialGoal>) {
        if (goals.isEmpty()) {
            binding.tvTotalSavedValue.text = "${symbol}0.00"
            binding.tvRemainingValue.text = "${symbol}.00"
            binding.tvAvgPerTimepDailyValue.text = "Daily: ${symbol}0.00"
            binding.tvAvgPerTimepWeeklyValue.text = "Weekly: ${symbol}0.00"
            binding.tvNearestGoalValue.text = "${symbol}0.00"
            binding.tvNearestGoalDeadlineValue.text = "No active goals"
            binding.progressGoals.setProgressCompat(0, true)
            binding.tvProgressGoalsPercent.text = "0%"
            return
        }

        val totalSaved = goals.sumOf { it.savedAmount }
        val remaining = goals.sumOf { it.targetAmount - it.savedAmount }
        val targetAmount = goals.sumOf { it.targetAmount }

        val nearestGoal =
            goals.filter { it.status == GoalStatus.ACTIVE }.minByOrNull { it.deadline }
        nearestGoal?.let {
            val rem = getDaysWeeksRemaining(it.deadline)
            binding.tvAvgPerTimepDailyValue.text =
                "Daily: $symbol${formatAmount(it.targetAmount / rem.daysRemaining)}"
            binding.tvAvgPerTimepWeeklyValue.text =
                "Weekly: $symbol${formatAmount(it.targetAmount / rem.weeksRemaining)}"
            binding.tvNearestGoalValue.text = "$symbol${formatAmount(it.targetAmount)}"
            binding.tvNearestGoalDeadlineValue.text =
                "${rem.daysRemainingFormatted} (${rem.dueDate})"
        }

        val progress = ((totalSaved / targetAmount) * 100).toInt().coerceAtMost(100)
        binding.tvTotalSavedValue.text = "$symbol${formatAmount(totalSaved)}"
        binding.tvRemainingValue.text = "$symbol${formatAmount(remaining)}"
        binding.progressGoals.setProgressCompat(progress, true)
        binding.tvProgressGoalsPercent.text = "$progress%"
    }

    private fun showPieChart(transactions: List<Transaction>) {
        val expenseByCategory = transactions.filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }.mapValues { it.value.sumOf { t -> t.amount } }

        val entries = expenseByCategory.map { PieEntry(it.value.toFloat(), it.key) }
        val colors = listOf(
            R.color.primary,
            R.color.income_green,
            R.color.expense_red,
            R.color.secondary,
            R.color.primary_dark
        ).map { ContextCompat.getColor(this, it) }

        binding.pieChart.apply {
            data = PieData(PieDataSet(entries, "").apply {
                sliceSpace = 10f; setDrawValues(true); this.colors = colors; valueTextSize = 12f
            })
            setUsePercentValues(true)
            description.isEnabled = false
            legend.isEnabled = true
            setEntryLabelColor(ContextCompat.getColor(context, R.color.transparent))
            invalidate()
        }
    }

    private fun showBarChart(transactions: List<Transaction>) {
        val cal = Calendar.getInstance()
        val days = mutableMapOf<String, Float>()
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        var current = Calendar.getInstance().apply { timeInMillis = rangeStart }
        while (current.timeInMillis <= rangeEnd) {
            days[sdf.format(current.time)] = 0f
            current.add(Calendar.DAY_OF_MONTH, 1)
        }

        transactions.forEach {
            val day = sdf.format(Date(it.date))
            days[day] = days.getOrDefault(day, 0f) + it.amount.toFloat()
        }

        val entries = days.entries.mapIndexed { idx, e -> BarEntry(idx.toFloat(), e.value) }
        val dataSet = BarDataSet(entries, "Spending").apply {
            color = ContextCompat.getColor(this@ReportsActivity, R.color.primary); valueTextSize =
            0f
        }
        binding.barChart.apply {
            data = BarData(dataSet).apply { barWidth = 0.5f }
            setFitBars(true)
            description.isEnabled = false
            legend.isEnabled = false
            animateY(600)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(days.keys.toList()); granularity =
                1f; position = XAxis.XAxisPosition.BOTTOM; setDrawGridLines(false); setDrawAxisLine(
                false
            )
            }
            axisLeft.apply { axisMinimum = 0f; setDrawGridLines(false); setDrawAxisLine(false) }
            axisRight.isEnabled = false
            invalidate()
        }
    }

    private fun getDaysWeeksRemaining(date: Long): TimeRemaining {
        val df = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val diff =
            ((date - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
        val daysText = when (diff) {
            0 -> "Today"; 1 -> "Tomorrow"; else -> "In $diff days"
        }
        return TimeRemaining(df.format(Date(date)), diff, daysText, (diff / 7.0).coerceAtLeast(1.0))
    }

    private fun formatDate(ms: Long) =
        SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(ms))

    private fun formatAmount(amount: Double) = String.format(Locale("en", "PH"), "%.2f", amount)
}
