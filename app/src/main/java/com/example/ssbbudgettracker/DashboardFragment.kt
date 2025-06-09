package com.example.ssbbudgettracker

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ssbbudgettracker.databinding.FragmentDashboardBinding
import com.example.ssbbudgettracker.model.Expense
import com.example.ssbbudgettracker.model.Goal
import com.example.ssbbudgettracker.model.Income
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val prefs = requireContext().getSharedPreferences("ssb_prefs", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getString("logged_in_user_id", "") ?: ""
        if (userId.isEmpty()) return

        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = cal.time
        cal.add(Calendar.MONTH, 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.add(Calendar.DATE, -1)
        val endDate = cal.time

        db.collection("users").document(userId).collection("expenses")
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .get()
            .addOnSuccessListener { expenseSnap ->
                val expenses = expenseSnap.toObjects(Expense::class.java)
                val totalExpense = expenses.sumOf { it.amount }
                val categoryTotals = mutableMapOf<String, Double>()

                expenses.forEach {
                    categoryTotals[it.category] = categoryTotals.getOrDefault(it.category, 0.0) + it.amount
                }

                drawPieChart(categoryTotals)
                binding.expenseAmount.text = "R%.2f".format(totalExpense)

                db.collection("users").document(userId).collection("incomes")
                    .whereGreaterThanOrEqualTo("date", startDate)
                    .whereLessThanOrEqualTo("date", endDate)
                    .get()
                    .addOnSuccessListener { incomeSnap ->
                        val incomes = incomeSnap.toObjects(Income::class.java)
                        val totalIncome = incomes.sumOf { it.amount }
                        val balance = totalIncome - totalExpense

                        binding.incomeAmount.text = "R%.2f".format(totalIncome)
                        binding.balanceAmount.text = "R%.2f".format(balance)

                        val monthKey = String.format("%tY-%tm", startDate, startDate)
                        db.collection("users").document(userId)
                            .collection("goals")
                            .document(monthKey)
                            .get()
                            .addOnSuccessListener { goalDoc ->
                                val goal = goalDoc.toObject(Goal::class.java)
                                updateGoalStatus(goal, totalExpense)
                            }
                            .addOnFailureListener {
                                binding.statusText.text = "No goal set"
                            }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load dashboard", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateGoalStatus(goal: Goal?, totalExpense: Double) {
        if (goal == null) {
            binding.statusText.text = "No goal data"
            return
        }

        val status: String
        val color: Int

        when {
            totalExpense < goal.minAmount -> {
                status = "You're under your minimum goal"
                color = Color.BLUE
            }
            totalExpense > goal.maxAmount -> {
                status = "You're over your goal!"
                color = Color.RED
            }
            else -> {
                status = "You're on track!"
                color = Color.GREEN
            }
        }

        binding.statusText.text = status
        binding.statusText.setTextColor(color)
    }

    private fun drawPieChart(data: Map<String, Double>) {
        val entries = data.map { PieEntry(it.value.toFloat(), it.key) }
        val dataSet = PieDataSet(entries, "Expenses")
        dataSet.setColors(
            Color.rgb(244, 67, 54),
            Color.rgb(76, 175, 80),
            Color.rgb(33, 150, 243),
            Color.rgb(255, 193, 7),
            Color.rgb(156, 39, 176)
        )

        val pieData = PieData(dataSet)
        binding.expensePieChart.apply {
            this.data = pieData
            description.isEnabled = false
            legend.isEnabled = true
            setUsePercentValues(true)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
