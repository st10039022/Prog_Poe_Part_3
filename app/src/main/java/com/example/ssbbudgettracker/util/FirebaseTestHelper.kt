package com.example.ssbbudgettracker.util

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseTestHelper {

    fun writeTestDocument() {
        val db = Firebase.firestore

        val testData = hashMapOf(
            "message" to "Hello from Firestore",
            "timestamp" to System.currentTimeMillis()
        )

        Log.d("FIRESTORE_TEST", "Attempting to write test document...")

        db.collection("TestCollection")
            .add(testData)
            .addOnSuccessListener { docRef ->
                Log.d("FIRESTORE_TEST", "Document added successfully with ID: ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE_TEST", "Error writing document: ${e.message}", e)
            }
    }
}
