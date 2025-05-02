package com.example.ssbbudgettracker

import android.app.DatePickerDialog
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
import java.text.SimpleDateFormat
import java.util.*

class AddIncomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddIncomeBinding
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)

        // Load categories
        lifecycleScope.launch(Dispatchers.IO) {
            val categories = db.incomeCategoryDao().getAllCategories().map { it.name }
            runOnUiThread {
                if (categories.isNotEmpty()) {
                    val adapter = ArrayAdapter(this@AddIncomeActivity, R.layout.spinner_item, categories)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.incomeCategorySpinner.adapter = adapter
                } else {
                    Toast.makeText(this@AddIncomeActivity, "Please add income categories first", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = dateFormat.format(calendar.time)
        binding.incomeDateEditText.setText(selectedDate)

        binding.incomeDateEditText.setOnClickListener {
            val dpd = DatePickerDialog(
                this,
                { _, year, month, day ->
                    val selected = Calendar.getInstance()
                    selected.set(year, month, day)
                    selectedDate = dateFormat.format(selected.time)
                    binding.incomeDateEditText.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }

        binding.saveIncomeButton.setOnClickListener {
            val category = binding.incomeCategorySpinner.selectedItem?.toString() ?: ""
            val amount = binding.incomeAmountEditText.text.toString().toDoubleOrNull()
            val description = binding.incomeDescriptionEditText.text.toString()

            if (category.isNotEmpty() && amount != null && description.isNotEmpty()) {
                val income = IncomeEntity(
                    category = category,
                    amount = amount,
                    description = description,
                    date = selectedDate
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    db.incomeDao().insertIncome(income)
                    finish()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
