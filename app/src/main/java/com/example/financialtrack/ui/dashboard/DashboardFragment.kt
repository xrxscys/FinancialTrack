package com.example.financialtrack.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.financialtrack.databinding.FragmentDashboardBinding
import com.example.financialtrack.ui.profile.ProfileActivity
import com.example.financialtrack.ui.notifications.NotificationActivity
import com.example.financialtrack.MainActivity
import com.example.financialtrack.R
import com.example.financialtrack.ui.transaction.TransactionActivity
import com.example.financialtrack.ui.transaction.TransactionAdapter // Import the TransactionAdapter
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import android.widget.LinearLayout
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()

    // Recent Transaction setup
    private lateinit var recyclerView: RecyclerView
    private lateinit var recentTransactionsAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindGreeting() // Bind the greeting message
        setupClicks() // Setup click listeners for buttons
        observeUi() // Observe budget usage percentage and update the progress bar

        // Set up RecyclerView for recent transactions
        recyclerView = view.findViewById(R.id.rvTransactions)
        recentTransactionsAdapter = TransactionAdapter(emptyList()) // Specify type explicitly
        recyclerView.adapter = recentTransactionsAdapter

        // Observe the recent transactions LiveData from ViewModel
        viewModel.recentTransactions.observe(viewLifecycleOwner, Observer { transactions ->
            recentTransactionsAdapter.updateTransactions(transactions) // Update the adapter with new data
        })

        // Fetch recent transactions
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        viewModel.fetchRecentTransactions(userId)

        // See all transactions button
        val btnSeeAllTransactions: MaterialButton = view.findViewById(R.id.btnSeeAllTransactions)
        btnSeeAllTransactions.setOnClickListener {
            // Navigate to the full transaction list (TransactionActivity)
            val intent = Intent(requireContext(), TransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun bindGreeting() {
        val user = FirebaseAuth.getInstance().currentUser
        val name = user?.displayName ?: user?.email?.substringBefore("@") ?: "there"
        binding.tvGreeting.text = "Hello, $name!"
    }

    private fun setupClicks() {
        // Profile button (navigate to ProfileActivity)
        binding.btnProfileTop.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        // Notifications button (navigate to Notifications activity)
        binding.btnNotifications.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        // Hamburger button (navigate to MainActivity)
        binding.btnHamburger.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }

    private fun observeUi() {
        // Observe total balance from ViewModel
        viewModel.totalBalance.observe(viewLifecycleOwner) { totalBalance ->
            binding.tvTotalBalance.text = "₱${formatString(totalBalance)}"
        }

        // Observe budget usage percentage from the ViewModel
        viewModel.budgetUsedPercent.observe(viewLifecycleOwner) { percent ->
            val usedPercentage = percent.toFloat()
            val remainingPercentage = 100 - usedPercentage

            // Update the progress bar segments
            val barUsed = binding.barUsed
            val barRemaining = binding.barRemaining
            val barOver = binding.barOver

            // Set the layout weight based on the percentage
            barUsed.layoutParams = (barUsed.layoutParams as LinearLayout.LayoutParams).apply {
                weight = usedPercentage / 100f
            }

            barRemaining.layoutParams = (barRemaining.layoutParams as LinearLayout.LayoutParams).apply {
                weight = remainingPercentage / 100f
            }

            // Handle over-spending (if any)
            if (percent > 100) {
                val overPercentage = percent - 100
                barOver.layoutParams = (barOver.layoutParams as LinearLayout.LayoutParams).apply {
                    weight = overPercentage / 100f
                }
            }

            // Update the text for Budget Health and the amount used vs total
            binding.tvBudgetHealthPercentage.text = "${usedPercentage.toInt()}% used"
            binding.tvBudgetAmount.text = "₱${viewModel.monthExpense.value?.toInt() ?: 0} / ₱${viewModel.monthIncome.value?.toInt() ?: 0}"
        }

        // Observe income from ViewModel
        viewModel.monthIncome.observe(viewLifecycleOwner) { income ->
            binding.tvIncomeAmount.text = "₱${formatString(income)}"
        }

        // Observe expense from ViewModel
        viewModel.monthExpense.observe(viewLifecycleOwner) { expense ->
            binding.tvExpenseAmount.text = "₱${formatString(expense)}"
        }
    }

    private fun formatString(amount: Double): String {
        return String.format(Locale("en", "PH"), "%.2f", amount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
