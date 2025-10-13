package com.example.financialtrack.ui.reports

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.repository.TransactionRepository

class ReportsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: TransactionRepository
    
    init {
        val transactionDao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(transactionDao)
    }
    
    fun getAllTransactions(userId: String): LiveData<List<Transaction>> {
        return repository.getAllTransactions(userId)
    }
}
