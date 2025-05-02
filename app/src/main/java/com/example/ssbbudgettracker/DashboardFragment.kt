package com.example.ssbbudgettracker

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())

        lifecycleScope.launch(Dispatchers.IO) {
            val expenses = db.expenseDao().getAllExpenses()
            val incomes = db.incomeDao().getAllIncomes()
            val goals = db.goalDao().getGoals()

            val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

            val totalExpense = expenses
                .filter { it.date.startsWith(currentMonth) }
                .sumOf { it.amount }

            val totalIncome = incomes
                .filter { it.date.startsWith(currentMonth) }
                .sumOf { it.amount }

            val balance = totalIncome - totalExpense

            val statusMessage: String
            val statusColor: Int

            if (goals != null) {
                statusMessage = when {
                    totalExpense > goals.maxAmount -> "You've exceeded your budget"
                    totalExpense < goals.minAmount -> "You're underspending"
                    else -> "You're on track!"
                }
                statusColor = when {
                    totalExpense > goals.maxAmount -> Color.RED
                    totalExpense < goals.minAmount -> Color.YELLOW
                    else -> Color.GREEN
                }
            } else {
                statusMessage = "No goals set"
                statusColor = Color.LTGRAY
            }

            val categorySums = expenses
                .filter { it.date.startsWith(currentMonth) }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            val entries = categorySums.map { (category, amount) ->
                PieEntry(amount.toFloat(), category)
            }

            val dataSet = PieDataSet(entries, "Expenses by Category")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueTextSize = 14f
            dataSet.valueTextColor = Color.WHITE

            val pieData = PieData(dataSet)

            withContext(Dispatchers.Main) {
                binding.incomeAmount.text = "R%.2f".format(totalIncome)
                binding.expenseAmount.text = "R%.2f".format(totalExpense)
                binding.balanceAmount.text = "R%.2f".format(balance)
                binding.statusText.text = statusMessage
                binding.statusText.setTextColor(statusColor)

                binding.expensePieChart.data = pieData
                binding.expensePieChart.description.isEnabled = false
                binding.expensePieChart.centerText = "Expenses"
                binding.expensePieChart.setEntryLabelColor(Color.BLACK)
                binding.expensePieChart.invalidate()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
