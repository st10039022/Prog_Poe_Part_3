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

    // sets up view binding for this screen
    private lateinit var binding: ActivityAddCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // dropdown options for category type
        val typeOptions = listOf("Expense", "Income")

        // sets up the spinner with "expense" and "income"
        val adapter = ArrayAdapter(this, R.layout.spinner_item, typeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categoryTypeSpinner.adapter = adapter

        // when save button is clicked
        binding.saveCategoryButton.setOnClickListener {
            val name = binding.categoryNameEditText.text.toString().trim() // get name
            val type = binding.categoryTypeSpinner.selectedItem.toString() // get selected type

            // show error if name is empty
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // create category object
            val category = CategoryEntity(name = name, type = type)

            // insert category into the database
            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(applicationContext)
                db.categoryDao().insertCategory(category)

                // show success message on the main thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddCategoryActivity, "Category saved", Toast.LENGTH_SHORT).show()
                    finish() // close the screen
                }
            }
        }
    }
}
