package com.example.ssbbudgettracker

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.data.CategoryEntity
import com.example.ssbbudgettracker.databinding.ActivityAddCategoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCategoryBinding

    private val iconList = listOf(
        "ic_shopping",
        "ic_food",
        "ic_transport",
        "ic_salary",
        "ic_entertainment"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(this, R.layout.spinner_item, iconList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.iconSpinner.adapter = adapter

        binding.saveCategoryButton.setOnClickListener {
            val name = binding.categoryNameEditText.text.toString()
            val icon = binding.iconSpinner.selectedItem.toString()

            if (name.isNotEmpty()) {
                val category = CategoryEntity(name = name, iconName = icon)
                lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getDatabase(this@AddCategoryActivity).categoryDao().insertCategory(category)
                    finish()
                }
            }
        }

        binding.iconSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                val iconName = iconList[position]
                val iconResId = resources.getIdentifier(iconName, "drawable", packageName)
                binding.previewIcon.setImageResource(iconResId)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }
}
