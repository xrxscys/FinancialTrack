package com.example.financialtrack.ui.debt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.Debt
import com.example.financialtrack.data.repository.DebtRepository
import kotlinx.coroutines.launch

class DebtViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: DebtRepository
    
    init {
        val debtDao = AppDatabase.getDatabase(application).debtDao()
        repository = DebtRepository(debtDao)
    }
    
    fun getAllDebts(userId: String): LiveData<List<Debt>> {
        return repository.getAllDebts(userId)
    }
    
    fun insertDebt(debt: Debt) = viewModelScope.launch {
        repository.insert(debt)
    }
    
    fun updateDebt(debt: Debt) = viewModelScope.launch {
        repository.update(debt)
    }
    
    fun deleteDebt(debt: Debt) = viewModelScope.launch {
        repository.delete(debt)
    }
}
