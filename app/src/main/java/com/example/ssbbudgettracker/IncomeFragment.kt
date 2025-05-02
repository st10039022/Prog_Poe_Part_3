package com.example.ssbbudgettracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ssbbudgettracker.data.AppDatabase
import com.example.ssbbudgettracker.data.IncomeEntity
import com.example.ssbbudgettracker.databinding.FragmentIncomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: IncomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = IncomeAdapter(emptyList())
        binding.incomeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.incomeRecyclerView.adapter = adapter

        binding.addIncomeFab.setOnClickListener {
            startActivity(Intent(requireContext(), AddIncomeActivity::class.java))
        }

        loadIncomes()
    }

    private fun loadIncomes() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            val incomes = db.incomeDao().getAllIncomes()

            withContext(Dispatchers.Main) {
                adapter.updateData(incomes)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
