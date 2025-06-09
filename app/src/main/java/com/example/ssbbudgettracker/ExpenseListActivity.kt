package com.example.ssbbudgettracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ssbbudgettracker.adapter.ExpenseAdapter
import com.example.ssbbudgettracker.databinding.ActivityExpenseListBinding
import com.example.ssbbudgettracker.model.Expense
import com.google.firebase.firestore.FirebaseFirestore

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseListBinding
    private lateinit var adapter: ExpenseAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ExpenseAdapter()
        binding.expenseRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.expenseRecyclerView.adapter = adapter

        loadExpenses()
    }

    private fun loadExpenses() {
        val prefs = getSharedPreferences("ssb_prefs", MODE_PRIVATE)
        val userId = prefs.getString("logged_in_user_id", "") ?: return

        db.collection("users").document(userId).collection("expenses")
            .get()
            .addOnSuccessListener { snapshot ->
                val expenses = snapshot.toObjects(Expense::class.java)
                adapter.updateData(expenses)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load expenses", Toast.LENGTH_SHORT).show()
            }
    }

}
