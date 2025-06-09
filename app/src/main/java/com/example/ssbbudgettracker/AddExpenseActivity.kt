package com.example.ssbbudgettracker

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ssbbudgettracker.model.Expense
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()
    private var selectedExpenseDate: Date? = null

    companion object {
        const val IMAGE_PICK_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val amountEditText = findViewById<EditText>(R.id.amountEditText)
        val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)
        val dateEditText = findViewById<EditText>(R.id.dateEditText)
        val saveButton = findViewById<Button>(R.id.saveExpenseButton)
        val selectPhotoButton = findViewById<Button>(R.id.selectPhotoButton)
        val photoPreview = findViewById<ImageView>(R.id.photoPreview)

        val categories = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        val sharedPrefs = getSharedPreferences("ssb_prefs", MODE_PRIVATE)
        val userId = sharedPrefs.getString("logged_in_user_id", "") ?: ""

        if (userId.isNotEmpty()) {
            db.collection("users").document(userId).collection("categories")
                .get()
                .addOnSuccessListener { snapshot ->
                    categories.clear()
                    categories.addAll(snapshot.documents.mapNotNull { it.getString("name") })
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show()
                }
        }

        dateEditText.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                cal.set(year, month, day)
                selectedExpenseDate = cal.time
                dateEditText.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedExpenseDate!!))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        selectPhotoButton.setOnClickListener {
            pickImage()
        }

        saveButton.setOnClickListener {
            val category = categorySpinner.selectedItem?.toString() ?: ""
            val amount = amountEditText.text.toString().toDoubleOrNull()
            val description = descriptionEditText.text.toString()

            if (amount == null) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId.isEmpty()) {
                Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val expense = Expense(
                id = UUID.randomUUID().toString(),
                category = category,
                amount = amount,
                description = description,
                date = selectedExpenseDate ?: Date(),
                photoUri = selectedImageUri?.toString() ?: "",
                userId = userId
            )

            db.collection("users").document(userId).collection("expenses")
                .document(expense.id)
                .set(expense)
                .addOnSuccessListener {
                    Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val inputUri = data.data
            inputUri?.let {
                try {
                    val inputStream = contentResolver.openInputStream(it)
                    val file = File(filesDir, "image_${System.currentTimeMillis()}.jpg")
                    val outputStream = FileOutputStream(file)

                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()

                    selectedImageUri = Uri.fromFile(file)

                    findViewById<ImageView>(R.id.photoPreview).apply {
                        setImageURI(selectedImageUri)
                        visibility = ImageView.VISIBLE
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Image loading failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
