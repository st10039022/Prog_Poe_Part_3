package com.example.ssbbudgettracker.repository

import com.example.ssbbudgettracker.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CategoryRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("categories")
    private var listener: ListenerRegistration? = null

    fun addCategory(category: Category, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val newDoc = collection.document()
        category.id = newDoc.id
        newDoc.set(category)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getAllCategories(
        onDataChanged: (List<Category>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        listener = collection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                onFailure(e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.toObjects(Category::class.java)
                onDataChanged(list)
            }
        }
    }

    fun removeListener() {
        listener?.remove()
    }
}
