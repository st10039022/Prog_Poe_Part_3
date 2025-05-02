package com.example.ssbbudgettracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ssbbudgettracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(DashboardFragment())

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> loadFragment(DashboardFragment())
                R.id.nav_expenses -> loadFragment(ExpensesFragment())
                R.id.nav_income -> loadFragment(IncomeFragment())
                R.id.nav_categories -> loadFragment(CategoriesFragment())
                R.id.nav_goals -> loadFragment(GoalsFragment()) // ✅ ADDED
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
