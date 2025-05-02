package com.example.ssbbudgettracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.databinding.ActivityIncomeListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IncomeListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIncomeListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.incomeRecyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch(Dispatchers.IO) {
            val incomes = AppDatabase.getDatabase(this@IncomeListActivity).incomeDao().getAllIncomes()
            withContext(Dispatchers.Main) {
                binding.incomeRecyclerView.adapter = IncomeAdapter(incomes)
            }
        }
    }
}
