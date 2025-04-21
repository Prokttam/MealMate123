// ReminderPagerAdapter.kt
package com.example.mealmate.ui.reminder

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ReminderPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UpcomingRemindersFragment()
            1 -> PastRemindersFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}