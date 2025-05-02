package com.example.ssbbudgettracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ssbbudgettracker.data.CategoryEntity
import com.example.ssbbudgettracker.databinding.ItemCategoryBinding

class CategoryAdapter(
    private var categories: List<CategoryEntity>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.categoryName.text = category.name
    }

    override fun getItemCount(): Int = categories.size

    fun updateData(newCategories: List<CategoryEntity>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}
