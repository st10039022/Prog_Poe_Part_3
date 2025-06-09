package com.example.ssbbudgettracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ssbbudgettracker.adapter.CategoryAdapter
import com.example.ssbbudgettracker.databinding.FragmentCategoriesBinding
import com.example.ssbbudgettracker.viewmodel.CategoryViewModel

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CategoryAdapter()
        binding.categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryRecyclerView.adapter = adapter

        viewModel.categories.observe(viewLifecycleOwner) { categoryList ->
            adapter.updateData(categoryList)
        }

        viewModel.error.observe(viewLifecycleOwner) { e ->
            e.printStackTrace()
        }

        binding.addCategoryFab.setOnClickListener {
            startActivity(Intent(requireContext(), AddCategoryActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
