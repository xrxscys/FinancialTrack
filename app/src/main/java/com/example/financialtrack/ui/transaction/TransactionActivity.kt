package com.example.financialtrack.ui.transaction


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.databinding.ActivityNotificationBinding
import com.example.financialtrack.databinding.ActivityTransactionBinding
import com.example.financialtrack.ui.notification.NotificationAdapter // ✅ import
import com.example.financialtrack.ui.transaction.TransactionAdapter

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
    }
}
