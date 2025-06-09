package com.example.ssbbudgettracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ssbbudgettracker.databinding.ActivitySetGoalsBinding
import com.example.ssbbudgettracker.model.Goal
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class SetGoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetGoalsBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveGoalButton.setOnClickListener {
            val minAmount = binding.minGoalEditText.text.toString().toDoubleOrNull()
            val maxAmount = binding.maxGoalEditText.text.toString().toDoubleOrNull()

            if (minAmount == null || maxAmount == null) {
                Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
            val goal = Goal(
                id = currentMonth,
                minAmount = minAmount,
                maxAmount = maxAmount,
                month = currentMonth
            )

            db.collection("goals").document(currentMonth)
                .set(goal)
                .addOnSuccessListener {
                    Toast.makeText(this, "Goals saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save goals", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
