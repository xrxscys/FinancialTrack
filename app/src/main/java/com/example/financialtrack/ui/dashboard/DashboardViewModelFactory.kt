package com.example.financialtrack.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financialtrack.data.repository.TransactionRepository

class DashboardViewModelFactory(
    private val repo: TransactionRepository,
    private val userId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repo, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
