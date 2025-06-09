package com.example.ssbbudgettracker.repository

import com.example.ssbbudgettracker.model.Goal
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class GoalRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("goals")
    private var listener: ListenerRegistration? = null

    fun setGoal(goal: Goal, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val docRef = collection.document(goal.month)
        goal.id = docRef.id
        docRef.set(goal)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getGoalForMonth(
        month: String,
        onData: (Goal?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        listener = collection.document(month).addSnapshotListener { snapshot, e ->
            if (e != null) {
                onFailure(e)
                return@addSnapshotListener
            }
            val goal = snapshot?.toObject(Goal::class.java)
            onData(goal)
        }
    }

    fun removeListener() {
        listener?.remove()
    }
}
