package com.example.financialtrack.data.repository

import androidx.lifecycle.LiveData
import com.example.financialtrack.data.database.TransactionDao
import com.example.financialtrack.data.model.Transaction
import com.example.financialtrack.data.model.TransactionType

class TransactionRepository(private val transactionDao: TransactionDao) {
    
    fun getAllTransactions(userId: String): LiveData<List<Transaction>> {
        return transactionDao.getAllTransactions(userId)
    }
    
    fun getTransactionsByType(userId: String, type: TransactionType): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsByType(userId, type)
    }
    
    suspend fun getTransactionById(id: Long): Transaction? {
        return transactionDao.getTransactionById(id)
    }
    
    suspend fun insert(transaction: Transaction): Long {
        return transactionDao.insert(transaction)
    }
    
    suspend fun update(transaction: Transaction) {
        transactionDao.update(transaction)
    }
    
    suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction)
    }
}
