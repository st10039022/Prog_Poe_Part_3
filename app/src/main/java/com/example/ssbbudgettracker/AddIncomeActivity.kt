package com.example.ssbbudgettracker

// import necessary libraries and packages
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.data.IncomeEntity
import com.example.ssbbudgettracker.databinding.ActivityAddIncomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

// activity to add income
class AddIncomeActivity : AppCompatActivity() {

    // view binding variable
    private lateinit var binding: ActivityAddIncomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initialize view binding
        binding = ActivityAddIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // load income categories from database
        loadIncomeCategories()

        // set current date in the date field
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        binding.incomeDateEditText.setText(currentDate)

        // handle save button click
        binding.saveIncomeButton.setOnClickListener {
            val category = binding.incomeCategorySpinner.selectedItem?.toString() ?: ""
            val amountText = binding.incomeAmountEditText.text.toString()
            val description = binding.incomeDescriptionEditText.text.toString()
            val date = currentDate

            // check if required fields are filled
            if (category.isEmpty() || amountText.isEmpty()) {
                Toast.makeText(this, "please enter all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // create income object
            val income = IncomeEntity(
                category = category,
                amount = amountText.toDouble(),
                description = description,
                date = date
            )

            // insert income into database on background thread
            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(applicationContext)
                db.incomeDao().insertIncome(income)

                // show toast and close screen on main thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddIncomeActivity, "income saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    // load income categories from the database and set to spinner
    private fun loadIncomeCategories() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            val categories = db.categoryDao().getCategoriesByType("Income")
            val categoryList = categories.map { it.name }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@AddIncomeActivity,
                    R.layout.spinner_item,
                    categoryList
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.incomeCategorySpinner.adapter = adapter
            }
        }
    }
}
