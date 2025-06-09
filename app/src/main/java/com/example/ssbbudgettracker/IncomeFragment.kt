package com.example.ssbbudgettracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ssbbudgettracker.adapter.IncomeAdapter
import com.example.ssbbudgettracker.databinding.FragmentIncomeBinding
import com.example.ssbbudgettracker.viewmodel.IncomeViewModel

class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IncomeViewModel by viewModels()
    private lateinit var adapter: IncomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = IncomeAdapter()
        binding.incomeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.incomeRecyclerView.adapter = adapter

        viewModel.incomes.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }

        viewModel.error.observe(viewLifecycleOwner) {
            it.printStackTrace()
        }

        binding.addIncomeFab.setOnClickListener {
            startActivity(Intent(requireContext(), AddIncomeActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
