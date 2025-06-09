package com.example.ssbbudgettracker

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ssbbudgettracker.databinding.ActivityDashboardBinding
import com.example.ssbbudgettracker.model.Expense
import com.example.ssbbudgettracker.model.Goal
import com.example.ssbbudgettracker.model.Income
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

        db.collection("expenses")
            .whereGreaterThanOrEqualTo("date", "$currentMonth-01")
            .whereLessThanOrEqualTo("date", "$currentMonth-31")
            .get()
            .addOnSuccessListener { expenseSnapshot ->
                val expenses = expenseSnapshot.toObjects(Expense::class.java)
                val totalExpense = expenses.sumOf { it.amount }

                val entries = mutableMapOf<String, Double>()
                expenses.forEach {
                    entries[it.category] = (entries[it.category] ?: 0.0) + it.amount
                }

                drawPieChart(entries)
                binding.expenseAmount.text = "R%.2f".format(totalExpense)

                db.collection("incomes")
                    .whereGreaterThanOrEqualTo("date", "$currentMonth-01")
                    .whereLessThanOrEqualTo("date", "$currentMonth-31")
                    .get()
                    .addOnSuccessListener { incomeSnapshot ->
                        val incomes = incomeSnapshot.toObjects(Income::class.java)
                        val totalIncome = incomes.sumOf { it.amount }
                        val balance = totalIncome - totalExpense

                        binding.incomeAmount.text = "R%.2f".format(totalIncome)
                        binding.balanceAmount.text = "R%.2f".format(balance)

                        db.collection("goals").document(currentMonth)
                            .get()
                            .addOnSuccessListener { goalDoc ->
                                val goal = goalDoc.toObject(Goal::class.java)
                                updateGoalStatus(goal, totalExpense)
                            }
                            .addOnFailureListener {
                                binding.statusText.text = "No goal set for this month."
                            }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load dashboard data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateGoalStatus(goal: Goal?, totalExpense: Double) {
        if (goal == null) {
            binding.statusText.text = "No goals set"
            return
        }

        val statusText: String
        val color: Int

        when {
            totalExpense < goal.minAmount -> {
                statusText = "You're under your minimum goal"
                color = Color.BLUE
            }
            totalExpense > goal.maxAmount -> {
                statusText = "You're over your maximum goal!"
                color = Color.RED
            }
            else -> {
                statusText = "You're within your goal range"
                color = Color.GREEN
            }
        }

        binding.statusText.text = statusText
        binding.statusText.setTextColor(color)
    }

    private fun drawPieChart(categoryTotals: Map<String, Double>) {
        val entries = categoryTotals.map { PieEntry(it.value.toFloat(), it.key) }
        val dataSet = PieDataSet(entries, "Expenses by Category")
        dataSet.setColors(
            Color.rgb(244, 67, 54),
            Color.rgb(33, 150, 243),
            Color.rgb(76, 175, 80),
            Color.rgb(255, 193, 7),
            Color.rgb(156, 39, 176)
        )

        val data = PieData(dataSet)
        binding.expensePieChart.apply {
            this.data = data
            description.isEnabled = false
            legend.isEnabled = true
            setUsePercentValues(true)
            invalidate()
        }
    }
}
