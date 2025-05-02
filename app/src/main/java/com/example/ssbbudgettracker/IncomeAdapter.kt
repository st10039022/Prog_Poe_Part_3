package com.example.ssbbudgettracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ssbbudgettracker.data.IncomeEntity
import com.example.ssbbudgettracker.databinding.ItemIncomeBinding

class IncomeAdapter(private val incomes: List<IncomeEntity>) :
    RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    inner class IncomeViewHolder(val binding: ItemIncomeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val binding = ItemIncomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IncomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val income = incomes[position]
        holder.binding.incomeCategoryText.text = income.category
        holder.binding.incomeAmountText.text = "R${income.amount}"
        holder.binding.incomeDateText.text = income.date
        holder.binding.incomeDescriptionText.text = income.description
    }

    override fun getItemCount(): Int = incomes.size
}
