package com.friends.ggiriggiri.ui.fifth.memory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentMemoryBinding
import com.friends.ggiriggiri.ui.adapter.MemoryPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator

class MemoryFragment : Fragment() {

    lateinit var fragmentMemoryBinding: FragmentMemoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentMemoryBinding = FragmentMemoryBinding.inflate(inflater, container, false)

        settingToolbarMemory()

        settingViewPager()

        // Inflate the layout for this fragment
        return fragmentMemoryBinding.root
    }

    // Toolbar
    private fun settingToolbarMemory(){
        fragmentMemoryBinding.apply {
            toolbarMemory.title = "추억들"
            toolbarMemory.menu.add(0,1,0,"알림")
                .setIcon(R.drawable.ic_notifications)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
    }

    // ViewPager2 설정
    private fun settingViewPager(){
        val adapter = MemoryPagerAdapter(this)
        fragmentMemoryBinding.memoryViewPager2.adapter = adapter

        // TabLayout과 ViewPager2 동기화
        TabLayoutMediator(fragmentMemoryBinding.tabLayoutLayout, fragmentMemoryBinding.memoryViewPager2) {tab, position ->
            tab.text = when(position){
                0 -> "요청"
                1 -> "질문"
                else -> ""
            }
        }.attach()
    }


}