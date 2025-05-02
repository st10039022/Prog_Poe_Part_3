package com.example.ssbbudgettracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.databinding.ActivityCategoryListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set up view binding
        binding = ActivityCategoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set layout manager for recycler view
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // load categories from database
        lifecycleScope.launch {
            val categories = withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(this@CategoryListActivity).categoryDao().getAllCategories()
            }
            // set adapter with category data
            binding.recyclerView.adapter = CategoryAdapter(categories)
        }
    }
}
