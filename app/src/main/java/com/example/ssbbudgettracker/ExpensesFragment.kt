package com.example.ssbbudgettracker

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ssbbudgettracker.adapter.ExpenseAdapter
import com.example.ssbbudgettracker.databinding.FragmentExpensesBinding
import com.example.ssbbudgettracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

class ExpensesFragment : Fragment() {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var adapter: ExpenseAdapter

    private var startDate: Date? = null
    private var endDate: Date? = null
    private var selectedCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ExpenseAdapter()
        binding.expensesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.expensesRecyclerView.adapter = adapter

        val categories = listOf("All", "Food", "Transport", "Health", "Entertainment")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = spinnerAdapter

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = if (position == 0) null else categories[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        binding.startDateButton.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                cal.set(year, month, day)
                startDate = cal.time
                binding.startDateText.text = dateFormat.format(startDate!!)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.endDateButton.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                cal.set(year, month, day)
                endDate = cal.time
                binding.endDateText.text = dateFormat.format(endDate!!)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.applyFilterButton.setOnClickListener {
            viewModel.loadExpenses(selectedCategory, startDate, endDate)
        }

        binding.addExpenseButton.setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }

        viewModel.expenses.observe(viewLifecycleOwner) { expenses ->
            adapter.updateData(expenses)
        }

        viewModel.error.observe(viewLifecycleOwner) { e ->
            e.printStackTrace()
        }

        viewModel.loadExpenses()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
