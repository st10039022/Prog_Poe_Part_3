package com.example.ssbbudgettracker.repository

import com.example.ssbbudgettracker.model.Income
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class IncomeRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("incomes")
    private var listener: ListenerRegistration? = null

    fun addIncome(income: Income, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val docRef = collection.document()
        income.id = docRef.id
        docRef.set(income)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getAllIncomes(onDataChanged: (List<Income>) -> Unit, onFailure: (Exception) -> Unit) {
        listener = collection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                onFailure(e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val incomes = snapshot.toObjects(Income::class.java)
                onDataChanged(incomes)
            }
        }
    }

    fun removeListener() {
        listener?.remove()
    }
}
