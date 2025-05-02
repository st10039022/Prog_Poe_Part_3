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
import com.example.ssbbudgettracker.data.CategoryEntity
import com.example.ssbbudgettracker.databinding.FragmentCategoriesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate the layout and set up view binding
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set up the adapter with an empty list
        adapter = CategoryAdapter(emptyList())
        binding.categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryRecyclerView.adapter = adapter

        // open add category screen when fab is clicked
        binding.addCategoryFab.setOnClickListener {
            startActivity(Intent(requireContext(), AddCategoryActivity::class.java))
        }

        // load categories from database
        loadCategories()
    }

    private fun loadCategories() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            val categories = db.categoryDao().getAllCategories()

            // update adapter on the main thread
            withContext(Dispatchers.Main) {
                adapter.updateData(categories)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // reload categories when returning to the screen
        loadCategories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear binding when view is destroyed
        _binding = null
    }
}
