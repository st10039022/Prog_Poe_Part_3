package com.example.ssbbudgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ssbbudgettracker.databinding.ActivityRegisterBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val email = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if user already exists
            db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        val userId = UUID.randomUUID().toString()
                        val newUser = hashMapOf(
                            "id" to userId,
                            "email" to email,
                            "password" to password
                        )

                        db.collection("users")
                            .document(userId)
                            .set(newUser)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to register", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error checking user", Toast.LENGTH_SHORT).show()
                }
        }

        binding.goToLoginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
