package com.example.ssbbudgettracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ssbbudgettracker.model.Goal
import com.example.ssbbudgettracker.repository.GoalRepository

class GoalViewModel : ViewModel() {

    private val repository = GoalRepository()

    private val _goal = MutableLiveData<Goal?>()
    val goal: LiveData<Goal?> get() = _goal

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> get() = _error

    fun setGoal(goal: Goal) {
        repository.setGoal(goal,
            onSuccess = { getGoal(goal.month) },
            onFailure = { _error.value = it }
        )
    }

    fun getGoal(month: String) {
        repository.getGoalForMonth(
            month = month,
            onData = { _goal.value = it },
            onFailure = { _error.value = it }
        )
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }
}
