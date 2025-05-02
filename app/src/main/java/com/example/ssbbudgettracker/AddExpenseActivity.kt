package com.example.ssbbudgettracker

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.data.ExpenseEntity
import com.example.ssbbudgettracker.databinding.ActivityAddExpenseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private var selectedDate: String = ""
    private var savedPhotoPath: String? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = saveImageToInternalStorage(it)
            if (savedPath != null) {
                savedPhotoPath = savedPath
                binding.photoPreview.setImageURI(Uri.fromFile(File(savedPath)))
                binding.photoPreview.visibility = android.view.View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch(Dispatchers.IO) {
            val categories = db.categoryDao().getAllCategories().map { it.name }
            runOnUiThread {
                if (categories.isNotEmpty()) {
                    val adapter = ArrayAdapter(this@AddExpenseActivity, R.layout.spinner_item, categories)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.categorySpinner.adapter = adapter
                } else {
                    Toast.makeText(this@AddExpenseActivity, "Please add a category first", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = dateFormat.format(calendar.time)
        binding.dateEditText.setText(selectedDate)

        binding.dateEditText.setOnClickListener {
            val dpd = DatePickerDialog(
                this,
                { _, year, month, day ->
                    val selected = Calendar.getInstance()
                    selected.set(year, month, day)
                    selectedDate = dateFormat.format(selected.time)
                    binding.dateEditText.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }

        binding.selectPhotoButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.saveExpenseButton.setOnClickListener {
            val category = binding.categorySpinner.selectedItem?.toString() ?: ""
            val amount = binding.amountEditText.text.toString().toDoubleOrNull()
            val description = binding.descriptionEditText.text.toString()

            if (category.isNotEmpty() && amount != null && description.isNotEmpty()) {
                val expense = ExpenseEntity(
                    category = category,
                    amount = amount,
                    description = description,
                    date = selectedDate,
                    photoUri = savedPhotoPath
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    db.expenseDao().insertExpense(expense)
                    finish()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = getFileName(uri)
            val file = File(filesDir, fileName)

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

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                cursor?.let {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            result = it.getString(nameIndex)
                        }
                    }
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "temp_image.jpg"
    }
}
