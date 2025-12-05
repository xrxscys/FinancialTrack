package com.example.financialtrack.ui.transaction


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import com.example.financialtrack.databinding.ActivityTransactionBinding

class TransactionActivity : AppCompatActivity(){

    private lateinit var binding: ActivityTransactionBinding

    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackTrans.setOnClickListener {
            finish()
        }

        val fakeTransaction = Transaction(
            userId = intent.getStringExtra("USER_ID").toString(),
            type = TransactionType.EXPENSE,
            category = "Food",
            description = "Jolly Hot dog",
            amount = 100.00,
            date = System.currentTimeMillis(),
        )
        val fakeTransactions = listOf(fakeTransaction)


        adapter = TransactionAdapter(fakeTransactions)
        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.adapter = adapter

        adapter.setOnClickListener { transaction ->
            val dialog = AddEditTransactionDialogFragment.newInstance(transaction)
//            dialog.setListener(this)
            dialog.show(supportFragmentManager, "TransactionDialog")
        }
    }
}
