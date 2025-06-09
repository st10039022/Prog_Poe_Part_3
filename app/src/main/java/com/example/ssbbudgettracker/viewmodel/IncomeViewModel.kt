package com.example.ssbbudgettracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ssbbudgettracker.model.Income
import com.example.ssbbudgettracker.repository.IncomeRepository

class IncomeViewModel : ViewModel() {

    private val repository = IncomeRepository()

    private val _incomes = MutableLiveData<List<Income>>()
    val incomes: LiveData<List<Income>> get() = _incomes

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> get() = _error

    init {
        loadIncomes()
    }

    fun loadIncomes() {
        repository.getAllIncomes(
            onDataChanged = { _incomes.value = it },
            onFailure = { _error.value = it }
        )
    }

    fun addIncome(income: Income) {
        repository.addIncome(income,
            onSuccess = { loadIncomes() },
            onFailure = { _error.value = it }
        )
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }
}
