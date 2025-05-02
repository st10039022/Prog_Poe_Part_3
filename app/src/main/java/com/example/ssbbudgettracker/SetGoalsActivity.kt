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

        binding.saveGoalButton.setOnClickListener {
            val minGoalText = binding.minGoalEditText.text.toString()
            val maxGoalText = binding.maxGoalEditText.text.toString()

            if (minGoalText.isEmpty() || maxGoalText.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val minGoal = minGoalText.toDouble()
            val maxGoal = maxGoalText.toDouble()

            if (minGoal > maxGoal) {
                Toast.makeText(this, "Minimum cannot be greater than maximum", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val goal = GoalEntity(id = 1, minAmount = minGoal, maxAmount = maxGoal)

            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(applicationContext)
                db.goalDao().insertGoal(goal)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SetGoalsActivity, "Goals saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
