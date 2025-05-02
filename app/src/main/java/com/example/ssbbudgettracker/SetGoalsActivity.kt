package com.example.ssbbudgettracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.data.GoalEntity
import com.example.ssbbudgettracker.databinding.ActivitySetGoalsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SetGoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetGoalsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            val goals = withContext(Dispatchers.IO) { db.goalDao().getGoals() }
            goals?.let {
                binding.minGoalEditText.setText(it.minAmount.toString())
                binding.maxGoalEditText.setText(it.maxAmount.toString())
            }
        }

        binding.saveGoalsButton.setOnClickListener {
            val min = binding.minGoalEditText.text.toString().toDoubleOrNull()
            val max = binding.maxGoalEditText.text.toString().toDoubleOrNull()

            if (min != null && max != null) {
                val goal = GoalEntity(minAmount = min, maxAmount = max)
                lifecycleScope.launch(Dispatchers.IO) {
                    db.goalDao().setGoals(goal)
                    finish()
                }
            } else {
                Toast.makeText(this, "Please enter valid amounts", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
