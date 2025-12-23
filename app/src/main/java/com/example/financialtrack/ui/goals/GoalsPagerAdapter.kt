package com.example.financialtrack.ui.goals

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class GoalsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CurrentGoalsFragment()
            1 -> HistoryGoalsFragment()
            else -> throw IllegalStateException()
        }
    }
}
