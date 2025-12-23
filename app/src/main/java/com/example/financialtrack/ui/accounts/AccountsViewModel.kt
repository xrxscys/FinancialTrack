package com.example.financialtrack.ui.accounts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.Account
import com.example.financialtrack.data.repository.AccountRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AccountsViewModel(application: Application) : AndroidViewModel(application) {
    private val accountRepository: AccountRepository
    val accounts: LiveData<List<Account>>

    init {
        val db = AppDatabase.getDatabase(application)
        accountRepository = AccountRepository(db.accountDao())
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        accounts = accountRepository.getAccountsByUser(userId)
    }

    fun insertAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.insert(account)
        }
    }
    fun updateAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.update(account)
        }
    }
}
