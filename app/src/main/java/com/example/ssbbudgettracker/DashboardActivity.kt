package com.example.ssbbudgettracker

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.databinding.ActivityDashboardBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set up view binding
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch(Dispatchers.IO) {
            val expenses = db.expenseDao().getAllExpenses()
            val incomes = db.incomeDao().getAllIncomes()
            val goals = db.goalDao().getGoals()

            val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

            // calculate total expense for current month
            val totalExpense = expenses
                .filter { it.date.startsWith(currentMonth) }
                .sumOf { it.amount }

            // calculate total income for current month
            val totalIncome = incomes
                .filter { it.date.startsWith(currentMonth) }
                .sumOf { it.amount }

            val balance = totalIncome - totalExpense

            val statusMessage: String
            val statusColor: Int

            // determine spending status based on goals
            if (goals != null) {
                statusMessage = when {
                    totalExpense > goals.maxAmount -> "you've exceeded your budget"
                    totalExpense < goals.minAmount -> "you're underspending"
                    else -> "you're on track!"
                }
                statusColor = when {
                    totalExpense > goals.maxAmount -> Color.RED
                    totalExpense < goals.minAmount -> Color.YELLOW
                    else -> Color.GREEN
                }
            } else {
                statusMessage = "no goals set"
                statusColor = Color.LTGRAY
            }

            // group expenses by category and calculate totals
            val categorySums = expenses
                .filter { it.date.startsWith(currentMonth) }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            // create pie chart entries
            val entries = categorySums.map { (category, amount) ->
                PieEntry(amount.toFloat(), category)
            }

            val dataSet = PieDataSet(entries, "expenses by category")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueTextSize = 14f
            dataSet.valueTextColor = Color.WHITE

            val pieData = PieData(dataSet)

            // update ui on main thread
            withContext(Dispatchers.Main) {
                binding.incomeAmount.text = "r%.2f".format(totalIncome)
                binding.expenseAmount.text = "r%.2f".format(totalExpense)
                binding.balanceAmount.text = "r%.2f".format(balance)
                binding.statusText.text = statusMessage
                binding.statusText.setTextColor(statusColor)

                binding.expensePieChart.data = pieData
                binding.expensePieChart.description.isEnabled = false
                binding.expensePieChart.centerText = "expenses"
                binding.expensePieChart.setEntryLabelColor(Color.BLACK)
                binding.expensePieChart.invalidate()
            }
        }
    }
}
