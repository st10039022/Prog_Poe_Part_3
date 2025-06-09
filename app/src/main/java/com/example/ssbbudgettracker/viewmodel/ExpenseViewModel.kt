package com.example.ssbbudgettracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ssbbudgettracker.model.Expense
import com.example.ssbbudgettracker.repository.ExpenseRepository
import java.util.*

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository()

    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> get() = _expenses

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> get() = _error

    fun loadExpenses(category: String? = null, startDate: Date? = null, endDate: Date? = null) {
        val prefs = getApplication<Application>().getSharedPreferences("ssb_prefs", Application.MODE_PRIVATE)
        val userId = prefs.getString("logged_in_user_id", "") ?: ""

        if (userId.isEmpty()) return

        repository.getExpensesForUser(
            userId = userId,
            category = category,
            startDate = startDate,
            endDate = endDate,
            onSuccess = { list -> _expenses.postValue(list) },
            onFailure = { e -> _error.postValue(e) }
        )
    }

    fun addExpense(expense: Expense) {
        repository.addExpense(
            expense,
            onSuccess = { loadExpenses() },
            onFailure = { e -> _error.postValue(e) }
        )
    }

    fun updateExpense(expense: Expense) {
        repository.updateExpense(
            expense,
            onSuccess = { loadExpenses() },
            onFailure = { e -> _error.postValue(e) }
        )
    }
}
