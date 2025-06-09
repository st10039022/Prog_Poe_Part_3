package com.example.ssbbudgettracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ssbbudgettracker.databinding.ActivityLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val user = snapshot.documents[0]
                        val storedPassword = user.getString("password")

                        if (storedPassword == password) {
                            val userId = user.getString("id") ?: user.id
                            getSharedPreferences("ssb_prefs", MODE_PRIVATE)
                                .edit()
                                .putString("logged_in_user_email", email)
                                .putString("logged_in_user_id", userId)
                                .apply()

                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.goToRegisterText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
