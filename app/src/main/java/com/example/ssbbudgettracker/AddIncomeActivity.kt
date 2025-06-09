package com.example.ssbbudgettracker

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ssbbudgettracker.databinding.ActivityAddIncomeBinding
import com.example.ssbbudgettracker.model.Income
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddIncomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddIncomeBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadIncomeCategories()
        setupDatePicker()

        binding.saveIncomeButton.setOnClickListener {
            val category = binding.incomeCategorySpinner.selectedItem?.toString() ?: ""
            val amount = binding.incomeAmountEditText.text.toString().toDoubleOrNull()
            val description = binding.incomeDescriptionEditText.text.toString().trim()
            val date = binding.incomeDateEditText.text.toString().trim()

            if (category.isEmpty() || amount == null || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val docRef = db.collection("incomes").document()
            val income = Income(
                id = docRef.id,
                category = category,
                amount = amount,
                description = description,
                date = date
            )

            docRef.set(income)
                .addOnSuccessListener {
                    Toast.makeText(this, "Income added", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add income", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadIncomeCategories() {
        db.collection("categories")
            .whereEqualTo("type", "income")
            .get()
            .addOnSuccessListener { snapshot ->
                val names = snapshot.documents.mapNotNull { it.getString("name") }

                if (names.isEmpty()) {
                    Toast.makeText(this, "No income categories found", Toast.LENGTH_SHORT).show()
                }

                val adapter = ArrayAdapter(this, R.layout.spinner_item, names)
                adapter.setDropDownViewResource(R.layout.spinner_item) // use your custom layout
                binding.incomeCategorySpinner.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load income categories: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setupDatePicker() {
        binding.incomeDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.incomeDateEditText.setText(selectedDate)
            }, year, month, day)

            datePickerDialog.show()
        }
    }
}
