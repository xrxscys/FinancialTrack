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
import com.example.financialtrack.data.model.TransferTargetType
import com.example.financialtrack.service.LoanPaymentProcessor

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository
    private val repository2: AccountRepository

    init {
        val transactionDao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(transactionDao)

        val accountDao = AppDatabase.getDatabase(application).accountDao()
        repository2 = AccountRepository(accountDao)
    }

    fun getTransactionsByTransferTarget(targetId: Int, targetType: TransferTargetType): LiveData<List<Transaction>> {
        return repository.getTransactionsByTransferTarget(targetId, targetType)
    }

    fun getAllTransactions(userId: String): LiveData<List<Transaction>> {
        return repository.getAllTransactions(userId)
    }

    fun getTransactionsByType(userId: String, type: TransactionType): LiveData<List<Transaction>> {
        return repository.getTransactionsByType(userId, type)
    }

    fun insertTransaction(transaction: Transaction, selectedLoanId: Long? = null) = viewModelScope.launch {
        repository.insert(transaction)
        // Process transaction for loan payments if a loan is selected
        if (selectedLoanId != null) {
            LoanPaymentProcessor(AppDatabase.getDatabase(getApplication())).processTransactionForLoanPayments(transaction, selectedLoanId)
        }
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


    fun performTransactionEdit(newTransaction: Transaction) = viewModelScope.launch {
        // grab the old transaction
        val oldTransaction = repository.getTransactionById(newTransaction.id)

        if (oldTransaction != null) {
            //delete old to go back to base
            updateAccountBalance(oldTransaction.accountId, oldTransaction, "Del")

            //soft update to move changes
            updateAccountBalance(newTransaction.accountId, newTransaction, "Add")

            //real update
            updateTransaction(newTransaction)
        }
    }

    fun deleteTransactionAndBalanceChange(transaction: Transaction) = viewModelScope.launch{
        updateAccountBalance(transaction.accountId, transaction, "Del")
        deleteTransaction(transaction)
    }

    fun insertTransactionAndBalanceChange(transaction: Transaction, selectedLoanId: Long? = null) = viewModelScope.launch{
        updateAccountBalance(transaction.accountId, transaction, "Add")
        insertTransaction(transaction, selectedLoanId)
    }
    suspend fun updateAccountBalance(accountId: Int, transaction: Transaction, tag: String) {
        //if the tag is del, reverse multiplier for balance change
        val multiplier = if (tag == "Del") -1.0 else 1.0


        val account = repository2.getAccountById(accountId)

        if (account != null) {
            var currentBalance = account.balance

            when (transaction.type) {
                TransactionType.INCOME -> {
                    currentBalance += (transaction.amount * multiplier)
                }
                TransactionType.EXPENSE -> {
                    currentBalance -= (transaction.amount * multiplier)
                }
                TransactionType.TRANSFER -> {
                    currentBalance -= (transaction.amount * multiplier)

                    val transferAccount = repository2.getAccountById(transaction.transferToId)
                    if (transferAccount != null) {
                        val newTargetBalance = transferAccount.balance + (transaction.amount * multiplier)

                        val updatedTransferAccount = transferAccount.copy(balance = newTargetBalance)
                        repository2.update(updatedTransferAccount)
                    }
                }
            }
            val updatedAccount = account.copy(balance = currentBalance)
            repository2.update(updatedAccount)
        }
    }

}
