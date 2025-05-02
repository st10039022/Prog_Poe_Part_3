package com.example.ssbbudgettracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ssbbudgettracker.data.CategoryEntity
import com.example.ssbbudgettracker.databinding.ItemCategoryBinding

class CategoryAdapter(
    private var categories: List<CategoryEntity>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    // view holder for each category item
    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        // inflate the layout for each item
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        // bind data to the view
        val category = categories[position]
        holder.binding.categoryName.text = category.name
    }

    override fun getItemCount(): Int = categories.size

    // update the list of categories and refresh the view
    fun updateData(newCategories: List<CategoryEntity>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}
