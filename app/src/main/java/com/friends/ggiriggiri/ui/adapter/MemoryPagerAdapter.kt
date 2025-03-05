package com.friends.ggiriggiri.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.friends.ggiriggiri.ui.fifth.questionlist.QuestionListFragment
import com.friends.ggiriggiri.ui.fifth.requestlist.RequestListFragment

class MemoryPagerAdapter(activity:Fragment) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> RequestListFragment()
            1 -> QuestionListFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

}