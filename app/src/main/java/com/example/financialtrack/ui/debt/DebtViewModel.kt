package com.example.financialtrack.ui.debt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.Debt
import com.example.financialtrack.data.repository.DebtRepository
import kotlinx.coroutines.launch

class DebtViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: DebtRepository
    private val _allDebts = MutableLiveData<List<Debt>>()
    val allDebts: LiveData<List<Debt>> = _allDebts
    
    private val _activeDebts = MutableLiveData<List<Debt>>()
    val activeDebts: LiveData<List<Debt>> = _activeDebts
    
    private val _paidDebts = MutableLiveData<List<Debt>>()
    val paidDebts: LiveData<List<Debt>> = _paidDebts
    
    private val _sortOption = MutableLiveData<SortOption>(SortOption.NEWEST_FIRST)
    val sortOption: LiveData<SortOption> = _sortOption
    
    init {
        val debtDao = AppDatabase.getDatabase(application).debtDao()
        repository = DebtRepository(debtDao)
    }
    
    fun getAllDebts(userId: String): LiveData<List<Debt>> {
        return repository.getAllDebts(userId)
    }
    
    fun getActiveDebts(userId: String) = viewModelScope.launch {
        val debts = repository.getActiveDebts(userId)
        _activeDebts.postValue(debts.sortByCreatedDate())
    }
    
    fun getPaidDebts(userId: String) = viewModelScope.launch {
        val debts = repository.getPaidDebts(userId)
        _paidDebts.postValue(debts.sortByCreatedDate())
    }
    
    fun sortDebts(option: SortOption) = viewModelScope.launch {
        _sortOption.postValue(option)
        val current = _activeDebts.value ?: return@launch
        val sorted = when (option) {
            SortOption.NEAREST_DEADLINE -> current.sortedBy { it.dueDate }
            SortOption.TITLE_A_Z -> current.sortedBy { it.creditorName }
            SortOption.TITLE_Z_A -> current.sortedByDescending { it.creditorName }
            SortOption.AMOUNT_LOW_HIGH -> current.sortedBy { it.amount }
            SortOption.AMOUNT_HIGH_LOW -> current.sortedByDescending { it.amount }
            SortOption.NEWEST_FIRST -> current.sortByCreatedDate()
        }
        _activeDebts.postValue(sorted)
    }
    
    fun insertDebt(debt: Debt) = viewModelScope.launch {
        repository.insert(debt)
        // Force a refresh of active debts to show new loan immediately
        val debts = repository.getActiveDebts(debt.userId)
        _activeDebts.postValue(debts.sortByCreatedDate())
    }
    
    fun updateDebt(debt: Debt) = viewModelScope.launch {
        repository.update(debt)
    }
    
    fun deleteDebt(debt: Debt) = viewModelScope.launch {
        repository.delete(debt)
    }
    
    private fun List<Debt>.sortByCreatedDate() = this.sortedBy { it.createdAt }
}

enum class SortOption {
    NEWEST_FIRST,
    NEAREST_DEADLINE,
    TITLE_A_Z,
    TITLE_Z_A,
    AMOUNT_LOW_HIGH,
    AMOUNT_HIGH_LOW
}
