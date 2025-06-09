package com.example.ssbbudgettracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ssbbudgettracker.databinding.ActivityAddIncomeCategoryBinding
import com.example.ssbbudgettracker.model.Category
import com.google.firebase.firestore.FirebaseFirestore

class AddIncomeCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddIncomeCategoryBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIncomeCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveIncomeCategoryButton.setOnClickListener {
            val name = binding.incomeCategoryNameEditText.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val docRef = db.collection("categories").document()
            val category = Category(
                id = docRef.id,
                name = name,
                type = "income" // Fixed: lowercase
            )

            docRef.set(category)
                .addOnSuccessListener {
                    Toast.makeText(this, "Income category added", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
