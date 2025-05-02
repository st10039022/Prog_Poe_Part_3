package com.example.ssbbudgettracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.data.IncomeCategoryEntity
import com.example.ssbbudgettracker.databinding.ActivityAddIncomeCategoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddIncomeCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddIncomeCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIncomeCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)

        binding.saveIncomeCategoryButton.setOnClickListener {
            val name = binding.incomeCategoryNameEditText.text.toString()
            if (name.isNotEmpty()) {
                val category = IncomeCategoryEntity(name = name)
                lifecycleScope.launch(Dispatchers.IO) {
                    db.incomeCategoryDao().insertCategory(category)
                    finish()
                }
            }
        }
    }
}
