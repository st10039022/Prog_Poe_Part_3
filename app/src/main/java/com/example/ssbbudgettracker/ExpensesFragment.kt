package com.example.ssbbudgettracker

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.data.ExpenseEntity
import com.example.ssbbudgettracker.databinding.FragmentExpensesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ExpensesFragment : Fragment() {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExpenseAdapter
    private var allExpenses: List<ExpenseEntity> = emptyList()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ExpenseAdapter(emptyList())
        binding.expensesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.expensesRecyclerView.adapter = adapter

        binding.addExpenseFab.setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }

        binding.startDateInput.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.startDateInput.setText(selectedDate)
            }
        }

        binding.endDateInput.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.endDateInput.setText(selectedDate)
            }
        }

        binding.filterButton.setOnClickListener {
            val startDate = binding.startDateInput.text.toString()
            val endDate = binding.endDateInput.text.toString()

            if (startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please select both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val filtered = allExpenses.filter {
                it.date >= startDate && it.date <= endDate
            }

            adapter.updateData(filtered)
            showCategoryTotals(filtered)
        }

        loadExpenses()
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val formatted = String.format("%04d-%02d-%02d", year, month + 1, day)
                onDateSelected(formatted)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showCategoryTotals(expenses: List<ExpenseEntity>) {
        val totalsByCategory = expenses.groupBy { it.category }
            .mapValues { it.value.sumOf { e -> e.amount } }

        val summary = totalsByCategory.entries.joinToString("\n") { (category, total) ->
            "$category: R%.2f".format(total)
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Category Totals")
            .setMessage(summary.ifEmpty { "No expenses found in this range." })
            .setPositiveButton("OK", null)
            .show()
    }

    private fun loadExpenses() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            val expenses = db.expenseDao().getAllExpenses()
            allExpenses = expenses

            withContext(Dispatchers.Main) {
                adapter.updateData(expenses)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
