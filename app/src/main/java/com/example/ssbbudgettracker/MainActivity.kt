package com.example.ssbbudgettracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ssbbudgettracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addCategoryButton.setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
        }

        binding.viewCategoriesButton.setOnClickListener {
            startActivity(Intent(this, CategoryListActivity::class.java))
        }

        binding.addExpenseButton.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        binding.viewExpensesButton.setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }
        binding.setGoalsButton.setOnClickListener {
            startActivity(Intent(this, SetGoalsActivity::class.java))
        }

        binding.addIncomeButton.setOnClickListener {
            startActivity(Intent(this, AddIncomeActivity::class.java))
        }

        binding.viewIncomeButton.setOnClickListener {
            startActivity(Intent(this, IncomeListActivity::class.java))
        }

        binding.addIncomeCategoryButton.setOnClickListener {
            startActivity(Intent(this, AddIncomeCategoryActivity::class.java))
        }

        binding.openDashboardButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

    }
}
