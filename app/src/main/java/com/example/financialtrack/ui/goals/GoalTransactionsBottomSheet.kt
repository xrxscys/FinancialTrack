package com.example.financialtrack.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.databinding.GoalTransactionsBottomSheetBinding
import com.example.financialtrack.data.model.TransferTargetType
import com.example.financialtrack.data.model.FinancialGoal
import com.example.financialtrack.ui.transaction.TransactionViewModel

class GoalTransactionsBottomSheet : BottomSheetDialogFragment() {
    private val goalsViewModel: GoalsViewModel by viewModels()
    private var _binding: GoalTransactionsBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val transactionViewModel: TransactionViewModel by viewModels()
    private lateinit var adapter: GoalTransactionsAdapter

    companion object {
        private const val ARG_GOAL_ID = "goal_id"
        private const val ARG_GOAL_NAME = "goal_name"
        fun newInstance(goalId: Int, goalName: String): GoalTransactionsBottomSheet {
            val fragment = GoalTransactionsBottomSheet()
            val args = Bundle()
            args.putInt(ARG_GOAL_ID, goalId)
            args.putString(ARG_GOAL_NAME, goalName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = GoalTransactionsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val goalId = arguments?.getInt(ARG_GOAL_ID) ?: return
        val goalName = arguments?.getString(ARG_GOAL_NAME) ?: "Goal"
        binding.tvGoalName.text = goalName
        adapter = GoalTransactionsAdapter()
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter
        // Observe transactions for this goal
        transactionViewModel.getTransactionsByTransferTarget(goalId, TransferTargetType.GOAL)
            .observe(viewLifecycleOwner) { txns ->
                adapter.submitList(txns)
            }
        // Observe the goal and wire up archive switch
        goalsViewModel.goalRepository.getGoalsByUser(goalsViewModel.userId).observe(viewLifecycleOwner) { allGoals ->
            val goal = allGoals.find { it.id == goalId } ?: return@observe
            binding.switchArchiveGoal.isChecked = goal.isArchived
            binding.tvArchiveLabel.text = if (goal.isArchived) "Unarchive Goal" else "Archive Goal"
            binding.switchArchiveGoal.setOnCheckedChangeListener { _, isChecked ->
                val updatedGoal = goal.copy(isArchived = isChecked)
                goalsViewModel.archiveGoal(updatedGoal)
                binding.tvArchiveLabel.text = if (isChecked) "Unarchive Goal" else "Archive Goal"
            }
            binding.btnEditGoal.setOnClickListener {
                val editDialog = EditGoalDialogFragment.newInstance(goal.id)
                editDialog.show(parentFragmentManager, "EditGoalDialogFragment")
                dismiss()
            }
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
