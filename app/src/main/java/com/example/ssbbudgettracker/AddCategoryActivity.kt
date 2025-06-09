package com.example.ssbbudgettracker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ssbbudgettracker.databinding.ActivityAddCategoryBinding
import com.example.ssbbudgettracker.model.Category
import com.google.firebase.firestore.FirebaseFirestore

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCategoryBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up spinner with "Income" and "Expense"
        val types = listOf("Income", "Expense")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categoryTypeSpinner.adapter = adapter

        // Save Category Button Logic
        binding.saveCategoryButton.setOnClickListener {
            val name = binding.categoryNameEditText.text.toString().trim()
            val type = binding.categoryTypeSpinner.selectedItem?.toString()?.lowercase() ?: ""

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val docRef = db.collection("categories").document()
            val category = Category(id = docRef.id, name = name, type = type)

            docRef.set(category)
                .addOnSuccessListener {
                    Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to add category: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
