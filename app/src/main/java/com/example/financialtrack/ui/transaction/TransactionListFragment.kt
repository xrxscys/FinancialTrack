package com.example.financialtrack.ui.transaction

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialtrack.data.model.TransactionType
import com.example.financialtrack.databinding.FragmentTransactionListBinding
import com.google.firebase.auth.FirebaseAuth

class TransactionListFragment: Fragment(){
    private var _binding : FragmentTransactionListBinding?= null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by activityViewModels()
    private lateinit var adapter: TransactionAdapter

    companion object {
        private const val ARG_TYPE = "transaction_type"
        fun newInstance (type: TransactionType): TransactionListFragment{
            val fragment = TransactionListFragment()
            val args = Bundle()
            Log.d("TransactionListFragment", "HJerlo")

            args.putSerializable(ARG_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val type = arguments?.getSerializable("transaction_type") as TransactionType

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        setupRecyclerView()
        if (userId.isNotEmpty()) {
            viewModel.getTransactionsByType(userId, type).observe(viewLifecycleOwner) { transactions ->
                adapter.updateTransactions(transactions)
            }
        }
    }

    private fun setupRecyclerView(){
        adapter = TransactionAdapter(emptyList())//passes an empty list, too lazy to change adapter cuh
        adapter.setOnClickListener{ transaction ->
            (activity as? TransactionActivity)?.showEditDialog(transaction)
        }
        binding.rvTransactions.layoutManager = LinearLayoutManager(context)
        binding.rvTransactions.adapter = adapter
    }

}
