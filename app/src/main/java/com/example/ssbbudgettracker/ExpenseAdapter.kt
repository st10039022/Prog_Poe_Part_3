package com.example.ssbbudgettracker

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ssbbudgettracker.data.ExpenseEntity
import com.example.ssbbudgettracker.databinding.ItemExpenseBinding
import java.io.File

class ExpenseAdapter(private val expenses: List<ExpenseEntity>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.binding.categoryText.text = expense.category
        holder.binding.amountText.text = "R${expense.amount}"
        holder.binding.dateText.text = expense.date
        holder.binding.descriptionText.text = expense.description

        if (!expense.photoUri.isNullOrEmpty()) {
            val imageFile = File(expense.photoUri)
            if (imageFile.exists()) {
                holder.binding.photoView.setImageURI(Uri.fromFile(imageFile))
                holder.binding.photoView.visibility = android.view.View.VISIBLE
            } else {
                holder.binding.photoView.visibility = android.view.View.GONE
            }
        } else {
            holder.binding.photoView.visibility = android.view.View.GONE
        }
    }

    override fun getItemCount(): Int = expenses.size
}
