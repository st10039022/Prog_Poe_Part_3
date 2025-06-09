package com.example.ssbbudgettracker

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ssbbudgettracker.databinding.FragmentGraphBinding
import com.example.ssbbudgettracker.model.Expense
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    private var startDate: Date? = null
    private var endDate: Date? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startDateBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                cal.set(year, month, day)
                startDate = cal.time
                binding.startDateText.text = dateFormat.format(startDate!!)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.endDateBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                cal.set(year, month, day)
                endDate = cal.time
                binding.endDateText.text = dateFormat.format(endDate!!)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.loadChartBtn.setOnClickListener {
            loadExpenses()
        }
    }

    private fun loadExpenses() {
        val prefs = requireContext().getSharedPreferences("ssb_prefs", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getString("logged_in_user_id", "") ?: return

        var query: Query = db.collection("users").document(userId).collection("expenses")

        if (startDate != null) {
            query = query.whereGreaterThanOrEqualTo("date", startDate!!)
        }
        if (endDate != null) {
            query = query.whereLessThanOrEqualTo("date", endDate!!)
        }

        query.get().addOnSuccessListener { result ->
            val categoryMap = HashMap<String, Double>()

            for (doc in result) {
                val expense = doc.toObject(Expense::class.java)
                val amount = expense.amount
                val category = expense.category
                categoryMap[category] = categoryMap.getOrDefault(category, 0.0) + amount
            }

            showChart(categoryMap)
        }
    }

    private fun showChart(categoryMap: Map<String, Double>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        categoryMap.entries.forEachIndexed { index, entry ->
            entries.add(BarEntry(index.toFloat(), entry.value.toFloat()))
            labels.add(entry.key)
        }

        val dataSet = BarDataSet(entries, "Amount Spent")
        val barData = BarData(dataSet)

        binding.barChart.apply {
            data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false
            description.isEnabled = false
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
