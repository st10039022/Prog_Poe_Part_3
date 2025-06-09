package com.example.ssbbudgettracker.repository

import android.util.Log
import com.example.ssbbudgettracker.model.Expense
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.util.*

class ExpenseRepository {

    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    fun getExpensesForUser(
        userId: String,
        category: String? = null,
        startDate: Date? = null,
        endDate: Date? = null,
        onSuccess: (List<Expense>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        var query: Query = db.collection("users").document(userId).collection("expenses")

        if (category != null && category != "All") {
            query = query.whereEqualTo("category", category)
        }
        if (startDate != null) {
            query = query.whereGreaterThanOrEqualTo("date", startDate)
        }
        if (endDate != null) {
            query = query.whereLessThanOrEqualTo("date", endDate)
        }

        query.get()
            .addOnSuccessListener { result ->
                val expenses = result.documents.mapNotNull { it.toObject(Expense::class.java) }
                Log.d("Firestore", "Fetched ${expenses.size} expenses for user $userId")
                onSuccess(expenses)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching expenses", exception)
                onFailure(exception)
            }
    }

    fun addExpense(expense: Expense, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = expense.userId
        db.collection("users").document(userId).collection("expenses").add(expense)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateExpense(expense: Expense, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = expense.userId
        val docId = expense.id ?: return
        db.collection("users").document(userId).collection("expenses").document(docId)
            .set(expense)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
