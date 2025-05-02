package com.example.ssbbudgettracker

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.databinding.ActivityExpenseListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseListBinding
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.expenseRecyclerView.layoutManager = LinearLayoutManager(this)

        val calendar = Calendar.getInstance()
        val today = dateFormat.format(calendar.time)
        val firstDay = dateFormat.format(calendar.apply { set(Calendar.DAY_OF_MONTH, 1) }.time)

        binding.startDateEditText.setText(firstDay)
        binding.endDateEditText.setText(today)

        binding.startDateEditText.setOnClickListener {
            showDatePicker { selected ->
                binding.startDateEditText.setText(selected)
            }
        }

        binding.endDateEditText.setOnClickListener {
            showDatePicker { selected ->
                binding.endDateEditText.setText(selected)
            }
        }

        binding.filterExpensesButton.setOnClickListener {
            val start = binding.startDateEditText.text.toString()
            val end = binding.endDateEditText.text.toString()

            if (start.isNotEmpty() && end.isNotEmpty()) {
                loadExpenses(start, end)
            } else {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
            }
        }

        loadExpenses(firstDay, today)
    }

    private fun showDatePicker(callback: (String) -> Unit) {
        val now = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selected = Calendar.getInstance()
                selected.set(year, month, dayOfMonth)
                callback(dateFormat.format(selected.time))
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadExpenses(startDate: String, endDate: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@ExpenseListActivity)
            val all = db.expenseDao().getAllExpenses()
            val filtered = all.filter {
                it.date >= startDate && it.date <= endDate
            }
            withContext(Dispatchers.Main) {
                binding.expenseRecyclerView.adapter = ExpenseAdapter(filtered)
            }
        }
    }
}
