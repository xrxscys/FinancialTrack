package com.example.financialtrack.ui.accounts

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.databinding.ActivityAccountsBinding
import com.example.financialtrack.data.model.Account
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.example.financialtrack.R
import com.example.financialtrack.data.model.AccountType
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountsBinding
    private val viewModel: AccountsViewModel by viewModels()
    private lateinit var accountAdapter: AccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accountAdapter = AccountAdapter()
        binding.rvAccounts.layoutManager = LinearLayoutManager(this)
        binding.rvAccounts.adapter = accountAdapter

        viewModel.accounts.observe(this) { accounts ->
            accountAdapter.submitList(accounts)
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnAddAccount.setOnClickListener {
            showAddAccountDialog()
        }
    }

    private fun showAddAccountDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_account, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_account_name)
        val spinnerType = dialogView.findViewById<Spinner>(R.id.spinner_account_type)
        val etBalance = dialogView.findViewById<EditText>(R.id.et_initial_balance)

        // Set up spinner with AccountType values
        val types = AccountType.values().map { it.name }
        spinnerType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)

        AlertDialog.Builder(this)
            .setTitle("Add Account")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val name = etName.text.toString().trim()
                val type = AccountType.valueOf(spinnerType.selectedItem.toString())
                val balance = etBalance.text.toString().toDoubleOrNull() ?: 0.0
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                if (name.isNotEmpty() && userId.isNotEmpty()) {
                    val account = Account(userId = userId, name = name, type = type, balance = balance)
                    viewModel.insertAccount(account)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
