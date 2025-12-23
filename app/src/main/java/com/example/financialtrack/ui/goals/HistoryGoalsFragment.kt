package com.example.financialtrack.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.databinding.FragmentGoalsListBinding
import com.example.financialtrack.data.model.GoalStatus

class HistoryGoalsFragment : Fragment() {
    private var _binding: FragmentGoalsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoalsViewModel by viewModels()
    private lateinit var adapter: GoalsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoalsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = GoalsAdapter(viewLifecycleOwner)
        binding.rvGoals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGoals.adapter = adapter
        viewModel.getGoalsByStatus(GoalStatus.COMPLETED, GoalStatus.EXPIRED).observe(viewLifecycleOwner) { goals ->
            val goalWithSavedList = goals.map { goal ->
                GoalWithSavedAmount(goal, viewModel.getSavedAmountForGoal(goal.id))
            }
            adapter.submitList(goalWithSavedList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
