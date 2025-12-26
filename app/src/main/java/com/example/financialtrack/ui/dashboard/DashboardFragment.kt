package com.example.financialtrack.ui.dashboard

import androidx.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.databinding.FragmentDashboardBinding
import com.example.financialtrack.ui.accounts.AccountsActivity
import com.example.financialtrack.ui.debt.DebtActivity
import com.example.financialtrack.ui.notifications.NotificationActivity
import com.example.financialtrack.ui.profile.ProfileActivity
import com.example.financialtrack.ui.reports.ReportsActivity
import com.example.financialtrack.ui.transaction.TransactionActivity
import com.example.financialtrack.ui.transaction.TransactionAdapter
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    }

    private lateinit var txAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindGreeting()

        setupRecycler()
        setupClicks()
        observeUi()
    }

    private fun bindGreeting() {
        val user = FirebaseAuth.getInstance().currentUser

        val name = user?.displayName
            ?.takeIf { it.isNotBlank() }
            ?: user?.email?.substringBefore("@")
            ?: "there"

        binding.tvGreeting.text = "Hello, $name!"
    }


    private fun setupRecycler() {
        txAdapter = TransactionAdapter(emptyList())
        txAdapter.setOnClickListener {
            // simplest: open the full Transactions screen
            startActivity(Intent(requireContext(), TransactionActivity::class.java))
        }

        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = txAdapter
    }

    private fun setupClicks() {
        binding.btnSeeAllTransactions.setOnClickListener {
            startActivity(Intent(requireContext(), TransactionActivity::class.java))
        }

        binding.toolbarDashboard.setNavigationOnClickListener {
            showQuickMenu()
        }
    }

    private fun observeUi() {
        viewModel.totalBalance.observe(viewLifecycleOwner) { total ->
            binding.tvTotalBalanceAmount.text = formatPhp(total)
        }
        viewModel.monthIncome.observe(viewLifecycleOwner) { inc ->
            binding.tvIncomeAmount.text = formatPhp(inc)
        }
        viewModel.monthExpense.observe(viewLifecycleOwner) { exp ->
            binding.tvExpenseAmount.text = formatPhp(exp)
        }
        viewModel.recentTransactions.observe(viewLifecycleOwner) { list ->
            txAdapter.updateTransactions(list)
        }
        viewModel.budgetUsedPercent.observe(viewLifecycleOwner) { percent ->
            // LinearProgressIndicator uses 0..100
            binding.progressBudget.progress = percent
        }
        viewModel.budgetPercentLabel.observe(viewLifecycleOwner) { label ->
            binding.tvBudgetHealthPercentage.text = label
        }
    }

    private fun showQuickMenu() {
        val items = arrayOf(
            "Accounts",
            "Transactions",
            "Budgets",
            "Reports",
            "Profile",
            "Notifications",
            "Debts & Loans"
        )

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Go to")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> startActivity(Intent(requireContext(), AccountsActivity::class.java))
                    1 -> startActivity(Intent(requireContext(), TransactionActivity::class.java))
                    2 -> Toast.makeText(requireContext(), "Budget screen not linked yet", Toast.LENGTH_SHORT).show()
                    3 -> startActivity(Intent(requireContext(), ReportsActivity::class.java))
                    4 -> {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            val intent = Intent(requireContext(), ProfileActivity::class.java)
                            intent.putExtra("USER_ID", user.uid)
                            startActivity(intent)
                        }
                    }
                    5 -> startActivity(Intent(requireContext(), NotificationActivity::class.java))
                    6 -> startActivity(Intent(requireContext(), DebtActivity::class.java))
                }
            }
            .show()
    }

    private fun formatPhp(amount: Double): String {
        val nf = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        return "₱${nf.format(amount)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
