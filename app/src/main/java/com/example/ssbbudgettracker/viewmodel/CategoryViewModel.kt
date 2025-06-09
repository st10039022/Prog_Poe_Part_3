package com.example.ssbbudgettracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ssbbudgettracker.model.Category
import com.example.ssbbudgettracker.repository.CategoryRepository

class CategoryViewModel : ViewModel() {

    private val repository = CategoryRepository()

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> get() = _error

    init {
        loadCategories()
    }

    fun loadCategories() {
        repository.getAllCategories(
            onDataChanged = { _categories.value = it },
            onFailure = { _error.value = it }
        )
    }

    fun addCategory(category: Category) {
        repository.addCategory(category,
            onSuccess = { loadCategories() },
            onFailure = { _error.value = it }
        )
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }
}
