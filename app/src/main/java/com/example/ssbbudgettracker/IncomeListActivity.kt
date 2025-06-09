package com.example.ssbbudgettracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ssbbudgettracker.adapter.IncomeAdapter
import com.example.ssbbudgettracker.databinding.ActivityIncomeListBinding
import com.example.ssbbudgettracker.model.Income
import com.google.firebase.firestore.FirebaseFirestore

class IncomeListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIncomeListBinding
    private lateinit var adapter: IncomeAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = IncomeAdapter()
        binding.incomeRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.incomeRecyclerView.adapter = adapter

        loadIncomeData()
    }

    private fun loadIncomeData() {
        db.collection("incomes")
            .get()
            .addOnSuccessListener { snapshot ->
                val incomeList = snapshot.toObjects(Income::class.java)
                adapter.updateData(incomeList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load income entries", Toast.LENGTH_SHORT).show()
            }
    }
}
