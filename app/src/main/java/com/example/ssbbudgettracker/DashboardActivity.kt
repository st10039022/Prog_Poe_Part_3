package com.example.ssbbudgettracker

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.databinding.ActivityDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)

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

            withContext(Dispatchers.Main) {
                binding.incomeAmount.text = "R%.2f".format(totalIncome)
                binding.expenseAmount.text = "R%.2f".format(totalExpense)
                binding.balanceAmount.text = "R%.2f".format(balance)
                binding.statusText.text = statusMessage
                binding.statusText.setTextColor(statusColor)
            }
        }
    }
}
