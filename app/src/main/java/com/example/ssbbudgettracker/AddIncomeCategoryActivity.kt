package com.example.ssbbudgettracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.data.IncomeCategoryEntity
import com.example.ssbbudgettracker.databinding.ActivityAddIncomeCategoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// activity to add a new income category
class AddIncomeCategoryActivity : AppCompatActivity() {

    // view binding variable
    private lateinit var binding: ActivityAddIncomeCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initialize view binding
        binding = ActivityAddIncomeCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get database instance
        val db = AppDatabase.getDatabase(this)

        // handle save button click
        binding.saveIncomeCategoryButton.setOnClickListener {
            val name = binding.incomeCategoryNameEditText.text.toString()
            // check if name is not empty
            if (name.isNotEmpty()) {
                // create category object
                val category = IncomeCategoryEntity(name = name)
                // insert category into database on background thread
                lifecycleScope.launch(Dispatchers.IO) {
                    db.incomeCategoryDao().insertCategory(category)
                    finish()
                }
            }
        }
    }
}
