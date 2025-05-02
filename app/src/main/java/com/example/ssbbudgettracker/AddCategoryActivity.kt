package com.example.ssbbudgettracker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.data.CategoryEntity
import com.example.ssbbudgettracker.databinding.ActivityAddCategoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typeOptions = listOf("Expense", "Income")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categoryTypeSpinner.adapter = adapter

        binding.saveCategoryButton.setOnClickListener {
            val name = binding.categoryNameEditText.text.toString().trim()
            val type = binding.categoryTypeSpinner.selectedItem.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = CategoryEntity(name = name, type = type)

            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(applicationContext)
                db.categoryDao().insertCategory(category)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddCategoryActivity, "Category saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
