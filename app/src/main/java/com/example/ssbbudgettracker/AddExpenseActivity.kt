package com.example.ssbbudgettracker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.data.ExpenseEntity
import com.example.ssbbudgettracker.databinding.ActivityAddExpenseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    // view binding for this screen
    private lateinit var binding: ActivityAddExpenseBinding

    // this stores the image uri the user selects
    private var selectedImageUri: Uri? = null

    // code to identify image picker result
    companion object {
        const val IMAGE_PICK_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // load categories that are marked as "expense"
        loadExpenseCategories()

        // set current date in the date field
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        binding.dateEditText.setText(currentDate)

        // when user clicks attach photo button
        binding.selectPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        // when user clicks save button
        binding.saveExpenseButton.setOnClickListener {
            val category = binding.categorySpinner.selectedItem?.toString() ?: ""
            val amountText = binding.amountEditText.text.toString()
            val description = binding.descriptionEditText.text.toString()
            val date = currentDate

            // show error if category or amount is empty
            if (category.isEmpty() || amountText.isEmpty()) {
                Toast.makeText(this, "Please enter all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // insert expense in background thread
            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(applicationContext)

                // if an image was selected, save it to internal storage
                val savedImageUri = selectedImageUri?.let { uri ->
                    copyImageToInternalStorage(uri)
                }

                // get user id from shared preferences
                val userId = getSharedPreferences("ssb_prefs", MODE_PRIVATE).getInt("logged_in_user_id", -1)

                // create expense object
                val expense = ExpenseEntity(
                    category = category,
                    amount = amountText.toDouble(),
                    description = description,
                    date = date,
                    photoUri = savedImageUri,
                    userId = userId
                )

                // insert expense into database
                db.expenseDao().insertExpense(expense)

                // show success message on the main thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddExpenseActivity, "Expense saved", Toast.LENGTH_SHORT).show()
                    finish() // close the screen
                }
            }
        }
    }

    // handles image result from picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.photoPreview.setImageURI(selectedImageUri)
            binding.photoPreview.visibility = android.view.View.VISIBLE
        }
    }

    // loads only expense categories into the spinner
    private fun loadExpenseCategories() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            val categories = db.categoryDao().getCategoriesByType("Expense")
            val categoryList = categories.map { it.name }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@AddExpenseActivity,
                    R.layout.spinner_item,
                    categoryList
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.categorySpinner.adapter = adapter
            }
        }
    }

    // copies selected image to internal storage and returns its path
    private fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(filesDir, "expense_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
