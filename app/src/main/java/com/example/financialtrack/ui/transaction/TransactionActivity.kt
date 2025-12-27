package com.example.financialtrack.ui.transaction


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.databinding.ActivityTransactionBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class TransactionActivity : AppCompatActivity(), AddEditTransactionDialogFragment.TransactionDialogListener{

    private lateinit var binding: ActivityTransactionBinding

    private lateinit var adapter: TransactionAdapter
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val pageAdapter = TransactionPagerAdapter(this)
        binding.viewPager.adapter = pageAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Expenses"
                1 -> "Income"
                else -> "Transfers"
            }
        }.attach()

        user?.let {userId = it.uid}

//        setupRecyclerView()

        setupBackButton()
        setupCreateButton()
//        loadTransactions(user)
    }

    private fun setupCreateButton(){
        binding.btnAddTrans.setOnClickListener {
            showCreateDialog()
        }
    }

    private fun setupRecyclerView(){
        adapter = TransactionAdapter(emptyList())//passes an empty list, too lazy to change adapter cuh
        adapter.setOnClickListener{ transaction ->
            showEditDialog(transaction)
        }
        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.adapter = adapter
    }

    private fun setupBackButton(){
        binding.btnBackTrans.setOnClickListener {
            finish()
        }
    }

    private fun loadTransactions(user : FirebaseUser?){
        if (user != null) {
            userId = user.uid

            viewModel.getAllTransactions(userId).observe(this) { transactions ->
                adapter.updateTransactions(transactions)
            }
        }
    }


    fun showEditDialog(transaction: Transaction){
        val dialog = AddEditTransactionDialogFragment.newInstance(transaction)
        dialog.setListener(this)
        dialog.show(supportFragmentManager, "EditTransactionDialog")
    }

    private fun showCreateDialog(){
        val dialog = AddEditTransactionDialogFragment.newInstanceForCreate(userId)
        dialog.setListener(this)
        dialog.show(supportFragmentManager, "AddTransactionDialog")
    }

    override fun onTransactionUpdate(transaction: Transaction){
        viewModel.performTransactionEdit(transaction)
    }

    override fun onTransactionDelete(transaction: Transaction) {
        viewModel.deleteTransactionAndBalanceChange(transaction)
    }

    override fun onTransactionCreated(transaction: Transaction, selectedLoanId: Long?) {
        viewModel.insertTransactionAndBalanceChange(transaction, selectedLoanId)
        binding.rvTransactions.smoothScrollToPosition(0)
    }
}
