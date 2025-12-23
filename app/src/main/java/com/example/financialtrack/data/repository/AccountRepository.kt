package com.example.financialtrack.data.repository

import androidx.lifecycle.LiveData
import com.example.financialtrack.data.database.AccountDao
import com.example.financialtrack.data.model.Account

class AccountRepository(private val accountDao: AccountDao) {
    fun getAccountsByUser(userId: String): LiveData<List<Account>> =
        accountDao.getAccountsByUser(userId)

    suspend fun insert(account: Account) = accountDao.insert(account)
    suspend fun update(account: Account) = accountDao.update(account)
    suspend fun delete(account: Account) = accountDao.delete(account)
    suspend fun getAccountById(accountId: Int): Account? = accountDao.getAccountById(accountId)
}
