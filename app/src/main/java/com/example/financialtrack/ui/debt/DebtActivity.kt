package com.example.financialtrack.ui.debt

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.data.model.Debt
import com.example.financialtrack.databinding.ActivityDebtBinding
import com.example.financialtrack.service.LoanNotificationManager
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.repository.DebtRepository
import com.example.financialtrack.data.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DebtActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDebtBinding
    private lateinit var debtViewModel: DebtViewModel
    private lateinit var activeDebtAdapter: DebtAdapter
    private lateinit var paidDebtAdapter: DebtAdapter
    private val activeDebtList = mutableListOf<Debt>()
    private val paidDebtList = mutableListOf<Debt>()
    private val userId = "user123" // Should get from current user
    private var currentSortOption = SortOption.NEWEST_FIRST
    private var isHistoryTabActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        debtViewModel = ViewModelProvider(this).get(DebtViewModel::class.java)

        setupAdapters()
        setupRecyclerViews()
        setupListeners()
        observeDebts()
        checkLoanNotifications()
    }

    override fun onResume() {
        super.onResume()
        // Check notifications every time user returns to this screen
        checkLoanNotifications()
    }

    private fun setupAdapters() {
        activeDebtAdapter = DebtAdapter(
            activeDebtList,
            false,
            { _ -> },
            { debt -> showDeleteConfirmation(debt) }
        )

        paidDebtAdapter = DebtAdapter(
            paidDebtList,
            true,
            { _ -> },
            { debt -> showDeleteConfirmation(debt) }
        )
    }

    private fun setupRecyclerViews() {
        binding.rvActiveDebts.apply {
            layoutManager = LinearLayoutManager(this@DebtActivity)
            adapter = activeDebtAdapter
        }

        binding.rvPaidDebts.apply {
            layoutManager = LinearLayoutManager(this@DebtActivity)
            adapter = paidDebtAdapter
        }
    }

    private fun setupListeners() {
        binding.btnBackDebt.setOnClickListener {
            finish()
        }

        binding.fabAddDebt.setOnClickListener {
            showAddDebtDialog()
        }

        binding.btnFilterSort.setOnClickListener {
            showSortDialog()
        }

        binding.fabClearHistory.setOnClickListener {
            clearAllHistoryLoans()
        }

        binding.toggleButtonsContainer.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> switchToActiveLoanTab()
                    1 -> switchToHistoryTab()
                }
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}

            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })

    }

    private fun switchToActiveLoanTab() {
        isHistoryTabActive = false
        binding.activeLoansContainer.visibility = View.VISIBLE
        binding.historyScrollContainer.visibility = View.GONE
        binding.fabClearHistory.visibility = View.GONE
    }

    private fun switchToHistoryTab() {
        isHistoryTabActive = true
        binding.activeLoansContainer.visibility = View.GONE
        binding.historyScrollContainer.visibility = View.VISIBLE
        binding.fabClearHistory.visibility = if (paidDebtList.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun observeDebts() {
        debtViewModel.getActiveDebts(userId)
        debtViewModel.activeDebts.observe(this) { debts ->
            activeDebtList.clear()
            activeDebtList.addAll(debts)
            activeDebtAdapter.notifyDataSetChanged()
            updateEmptyState()
        }

        debtViewModel.getPaidDebts(userId)
        debtViewModel.paidDebts.observe(this) { debts ->
            paidDebtList.clear()
            paidDebtList.addAll(debts)
            paidDebtAdapter.notifyDataSetChanged()
            updateHistoryEmptyState()
        }
    }

    private fun showAddDebtDialog() {
        AddEditDebtDialogFragment(null) { newDebt ->
            debtViewModel.insertDebt(newDebt)
        }.show(supportFragmentManager, "AddDebtDialog")
    }

    private fun showSortDialog() {
        val options = arrayOf(
            "📅 Nearest deadline",
            "🔤 Title A–Z",
            "🔤 Title Z–A",
            "💰 Amount (lowest → highest)",
            "💰 Amount (highest → lowest)",
            "📌 Newest first"
        )

        AlertDialog.Builder(this)
            .setTitle("Sort Loans")
            .setItems(options) { _, which ->
                currentSortOption = when (which) {
                    0 -> SortOption.NEAREST_DEADLINE
                    1 -> SortOption.TITLE_A_Z
                    2 -> SortOption.TITLE_Z_A
                    3 -> SortOption.AMOUNT_LOW_HIGH
                    4 -> SortOption.AMOUNT_HIGH_LOW
                    else -> SortOption.NEWEST_FIRST
                }
                debtViewModel.sortDebts(currentSortOption)
            }
            .show()
    }

    private fun clearAllHistoryLoans() {
        AlertDialog.Builder(this)
            .setTitle("Clear History?")
            .setMessage("Delete all paid loans from history? This cannot be undone.")
            .setPositiveButton("Delete All") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        for (paidDebt in paidDebtList) {
                            debtViewModel.deleteDebt(paidDebt)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                refreshDebtLists()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun refreshDebtLists() {
        debtViewModel.getActiveDebts(userId)
        debtViewModel.getPaidDebts(userId)
    }

    private fun showDeleteConfirmation(debt: Debt) {
        AlertDialog.Builder(this)
            .setTitle("Delete Loan")
            .setMessage("Delete \"${debt.creditorName}\" (₱${String.format("%.2f", debt.amount)})?\n\nThis action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        debtViewModel.deleteDebt(debt)
                        // Refresh immediately after deletion completes
                        runOnUiThread {
                            refreshDebtLists()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            android.widget.Toast.makeText(
                                this@DebtActivity,
                                "Error deleting loan",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private suspend fun sendLoanDeletedNotification(debt: Debt) {
        try {
            val database = AppDatabase.getDatabase(this)
            val notificationRepository = NotificationRepository(database.notificationDao())
            val debtRepository = DebtRepository(database.debtDao())
            val notificationManager = LoanNotificationManager(
                this,
                notificationRepository,
                debtRepository
            )
            notificationManager.sendLoanDeletedNotification(debt)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun sendLoanAddedNotification(debt: Debt) {
        try {
            val database = AppDatabase.getDatabase(this)
            val notificationRepository = NotificationRepository(database.notificationDao())
            val debtRepository = DebtRepository(database.debtDao())
            val notificationManager = LoanNotificationManager(
                this,
                notificationRepository,
                debtRepository
            )
            notificationManager.sendLoanAddedNotification(debt)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkLoanNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(this@DebtActivity)
                val debtRepository = DebtRepository(database.debtDao())
                val notificationRepository = NotificationRepository(database.notificationDao())
                val notificationManager = LoanNotificationManager(
                    this@DebtActivity,
                    notificationRepository,
                    debtRepository
                )
                // Check all active loans for notifications
                notificationManager.checkLoanNotifications()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateEmptyState() {
        val isEmpty = activeDebtList.isEmpty()
        binding.emptyStateActiveLoan.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.activeLoansContent.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun updateHistoryEmptyState() {
        val isEmpty = paidDebtList.isEmpty()
        binding.emptyStateHistory.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.historyContent.visibility = if (isEmpty) View.GONE else View.VISIBLE
        // Hide clear button if no history
        if (isHistoryTabActive) {
            binding.fabClearHistory.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
    }
}
