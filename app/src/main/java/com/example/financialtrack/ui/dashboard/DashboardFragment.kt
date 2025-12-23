package com.example.financialtrack.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.R
import com.example.financialtrack.data.repository.TransactionRepository
import com.example.financialtrack.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Repository & Factory
        val transactionDao = AppDatabase.getDatabase(requireContext()).transactionDao()
        val repo = TransactionRepository(transactionDao)
        val userId = "currentUserId" // replace with your auth user ID

        val factory = DashboardViewModelFactory(repo, userId)
        viewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)

        setupObservers()
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.income.observe(viewLifecycleOwner) { income ->
            binding.tvIncomeAmount.text =
                String.format(getString(R.string.dashboard_income_amount), income)
        }

        viewModel.expense.observe(viewLifecycleOwner) { expense ->
            binding.tvExpenseAmount.text =
                String.format(getString(R.string.dashboard_expense_amount), expense)
        }

        viewModel.categories.observe(viewLifecycleOwner) { map ->
            setupPieChart(map)
        }
    }

    private fun setupPieChart(data: Map<String, Double>) {
        val entries = ArrayList<PieEntry>()
        data.forEach { (category, amount) ->
            entries.add(PieEntry(amount.toFloat(), category))
        }

        val dataSet = PieDataSet(entries, "Category Breakdown")

        // Standard chart colors (Google Charts / GForms style)
        val chartColors = listOf(
            0xFF3366CC.toInt(),
            0xFFDC3912.toInt(),
            0xFFFF9900.toInt(),
            0xFF109618.toInt(),
            0xFF990099.toInt(),
            0xFF0099C6.toInt(),
            0xFFFFF200.toInt(),
            0xFFB82E2E.toInt(),
            0xFF316395.toInt(),
            0xFF994499.toInt()
        )

        // Dynamically assign colors to each entry
        val colors = mutableListOf<Int>()
        for (i in entries.indices) {
            colors.add(chartColors[i % chartColors.size])
        }
        dataSet.colors = colors

        val pieData = PieData(dataSet)
        binding.pieChart.data = pieData
        binding.pieChart.invalidate()
    }

    private fun setupRecyclerView() {
        binding.rvCategoryBreakdown.layoutManager = LinearLayoutManager(requireContext())
        // Adapter will be added after you decide on category item layout
    }

    private fun setupClickListeners() {
        binding.btnPrev.setOnClickListener {
            // Handle previous month
        }
        binding.btnNext.setOnClickListener {
            // Handle next month
        }
        binding.dropdownPeriod.setOnClickListener {
            // Handle dropdown menu
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
