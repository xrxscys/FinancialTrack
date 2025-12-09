package com.example.financialtrack.ui.transaction


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import com.example.financialtrack.databinding.ActivityTransactionBinding
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class TransactionActivity : AppCompatActivity(), AddEditTransactionDialogFragment.TransactionDialogListener{

    private lateinit var binding: ActivityTransactionBinding

    private lateinit var adapter: TransactionAdapter
    private val transactionList = mutableListOf<Transaction>()
    private var userId: String = "" //placeholder

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        setupRecyclerView()
        setupBackButton()
        loadTransactions(user)
    }

    private fun setupRecyclerView(){
        adapter = TransactionAdapter(transactionList)
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
        userId = user!!.uid


        transactionList.clear()
        transactionList.addAll(getFakeTransactions(userId))
        Log.d("name", transactionList[1].userId)
        adapter.updateTransactions(transactionList)
    }

    private fun getFakeTransactions(userId: String): List<Transaction> {
        return listOf(
            Transaction(
                id = 1,
                userId = userId,
                type = TransactionType.EXPENSE,
                category = "Food",
                description = "Jolly Hot dog",
                amount = 100.00,
                date = System.currentTimeMillis()
            ),
            Transaction(
                id = 2,
                userId = userId,
                type = TransactionType.INCOME,
                category = "Salary",
                description = "Monthly salary",
                amount = 50000.00,
                date = System.currentTimeMillis() - 86400000
            ),
            Transaction(
                id = 3,
                userId = userId,
                type = TransactionType.EXPENSE,
                category = "Transport",
                description = "Grab ride",
                amount = 250.00,
                date = System.currentTimeMillis() - 172800000
            )
        )
    }

    private fun showEditDialog(transaction: Transaction){
        val dialog = AddEditTransactionDialogFragment.newInstance(transaction)
        dialog.setListener(this)
        dialog.show(supportFragmentManager, "EditTransactionDialog")
    }

    override fun onTransactionUpdate(transaction: Transaction){
        val index = transactionList.indexOfFirst {
            it.id == transaction.id
        }
        if (index != -1){
            transactionList[index] = transaction
            adapter.updateTransactions(transactionList)

            //add database change here
        }
    }

    override fun onTransactionDelete(transaction: Transaction) {
        transactionList.removeAll {
            it.id == transaction.id
        }
        adapter.updateTransactions(transactionList)
    }
}
