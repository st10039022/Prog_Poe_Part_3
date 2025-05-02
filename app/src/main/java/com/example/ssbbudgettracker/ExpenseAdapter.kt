package com.example.ssbbudgettracker

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ssbbudgettracker.data.ExpenseEntity
import com.example.ssbbudgettracker.databinding.ItemExpenseBinding
import java.io.File
import java.io.FileInputStream

class ExpenseAdapter(private var expenses: List<ExpenseEntity>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.binding.categoryTextView.text = expense.category
        holder.binding.amountTextView.text = "R%.2f".format(expense.amount)
        holder.binding.descriptionTextView.text = expense.description
        holder.binding.dateTextView.text = expense.date

        if (!expense.photoUri.isNullOrEmpty()) {
            try {
                val file = File(expense.photoUri)
                if (file.exists()) {
                    val inputStream = FileInputStream(file)
                    val drawable = Drawable.createFromStream(inputStream, file.name)
                    if (drawable != null) {
                        holder.binding.photoImageView.setImageDrawable(drawable)
                        holder.binding.photoImageView.visibility = View.VISIBLE
                    } else {
                        holder.binding.photoImageView.visibility = View.GONE
                    }
                } else {
                    holder.binding.photoImageView.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                holder.binding.photoImageView.visibility = View.GONE
            }
        } else {
            holder.binding.photoImageView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = expenses.size

    fun updateData(newExpenses: List<ExpenseEntity>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
