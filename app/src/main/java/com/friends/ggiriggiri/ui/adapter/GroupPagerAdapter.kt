package com.friends.ggiriggiri.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.friends.ggiriggiri.ui.settinggroup.joingroup.JoinGroupFragment
import com.friends.ggiriggiri.ui.settinggroup.makegroup.MakeGroupFragment

class GroupPagerAdapter(activity: Fragment) : FragmentStateAdapter(activity){
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> MakeGroupFragment()
            1 -> JoinGroupFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

}