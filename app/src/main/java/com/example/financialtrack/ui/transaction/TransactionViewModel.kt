package com.example.financialtrack.ui.transaction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType
import com.example.financialtrack.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import com.example.financialtrack.data.repository.AccountRepository
import com.example.financialtrack.data.model.Account
import com.example.financialtrack.data.model.AccountType

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository
    private val repository2: AccountRepository

    init {
        val transactionDao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(transactionDao)

        val accountDao = AppDatabase.getDatabase(application).accountDao()
        repository2 = AccountRepository(accountDao)
    }

    fun getAllTransactions(userId: String): LiveData<List<Transaction>> {
        return repository.getAllTransactions(userId)
    }

    fun getTransactionsByType(userId: String, type: TransactionType): LiveData<List<Transaction>> {
        return repository.getTransactionsByType(userId, type)
    }

    fun insertTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insert(transaction)
    }

    fun updateTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.update(transaction)
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.delete(transaction)
    }

    fun getAllAccounts(userId: String): LiveData<List<Account>> {
        return repository2.getAccountsByUser(userId)
    }
}
