package com.example.financialtrack.ui.transaction

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.financialtrack.data.model.TransactionType

class TransactionPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> TransactionListFragment.newInstance(TransactionType.EXPENSE)
            1-> TransactionListFragment.newInstance(TransactionType.INCOME)
            else-> TransactionListFragment.newInstance(TransactionType.TRANSFER)
        }
    }
}
