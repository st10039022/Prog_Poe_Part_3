package com.example.ssbbudgettracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ssbbudgettracker.data.ExpenseEntity
import com.example.ssbbudgettracker.databinding.ItemExpenseBinding

class ExpenseAdapter(
    private var expenses: List<ExpenseEntity>
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        holder.binding.expenseCategory.text = expense.category
        holder.binding.expenseAmount.text = "R%.2f".format(expense.amount)
        holder.binding.expenseDescription.text = expense.description
        holder.binding.expenseDate.text = expense.date
    }

    override fun getItemCount(): Int = expenses.size

    fun updateData(newExpenses: List<ExpenseEntity>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
