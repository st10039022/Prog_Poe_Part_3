package com.example.ssbbudgettracker.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ssbbudgettracker.R
import com.example.ssbbudgettracker.model.Expense
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var expenses: List<Expense> = emptyList()

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.categoryTextView.text = expense.category
        holder.amountTextView.text = "R%.2f".format(expense.amount)
        holder.descriptionTextView.text = expense.description

        //Format the date for display
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        holder.dateTextView.text = expense.date?.let { formatter.format(it) } ?: "No date"

        if (expense.photoUri.isNotEmpty()) {
            holder.photoImageView.visibility = View.VISIBLE
            holder.photoImageView.setImageURI(Uri.parse(expense.photoUri))
        } else {
            holder.photoImageView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = expenses.size

    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
