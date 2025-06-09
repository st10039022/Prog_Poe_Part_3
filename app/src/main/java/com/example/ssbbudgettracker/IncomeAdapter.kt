package com.example.ssbbudgettracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ssbbudgettracker.databinding.ItemIncomeBinding
import com.example.ssbbudgettracker.model.Income

class IncomeAdapter : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    private var incomes: List<Income> = emptyList()

    inner class IncomeViewHolder(val binding: ItemIncomeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val binding = ItemIncomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IncomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val income = incomes[position]
        holder.binding.incomeCategory.text = income.category
        holder.binding.incomeAmount.text = "R%.2f".format(income.amount)
        holder.binding.incomeDescription.text = income.description
        holder.binding.incomeDate.text = income.date
    }

    override fun getItemCount(): Int = incomes.size

    fun updateData(newList: List<Income>) {
        incomes = newList
        notifyDataSetChanged()
    }
}
