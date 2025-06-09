package com.example.ssbbudgettracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ssbbudgettracker.adapter.CategoryAdapter
import com.example.ssbbudgettracker.databinding.ActivityCategoryListBinding
import com.example.ssbbudgettracker.model.Category
import com.google.firebase.firestore.FirebaseFirestore

class CategoryListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryListBinding
    private lateinit var adapter: CategoryAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = CategoryAdapter()
        binding.categoryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.categoryRecyclerView.adapter = adapter

        loadAllCategories()
    }

    private fun loadAllCategories() {
        db.collection("categories")
            .get()
            .addOnSuccessListener { snapshot ->
                val categories = snapshot.toObjects(Category::class.java)
                adapter.updateData(categories)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
    }
}
