package com.example.ssbbudgettracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ssbbudgettracker.databinding.FragmentGoalsBinding
import com.example.ssbbudgettracker.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.*

class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val month = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        viewModel.getGoal(month)

        viewModel.goal.observe(viewLifecycleOwner) { goal ->
            if (goal != null) {
                binding.minGoalText.text = "Min Goal: R${goal.minAmount}"
                binding.maxGoalText.text = "Max Goal: R${goal.maxAmount}"
            } else {
                binding.minGoalText.text = "No goal set"
                binding.maxGoalText.text = ""
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            it.printStackTrace()
        }

        binding.setGoalsButton.setOnClickListener {
            startActivity(Intent(requireContext(), SetGoalsActivity::class.java))
        }

        binding.viewGraphButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GraphFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
