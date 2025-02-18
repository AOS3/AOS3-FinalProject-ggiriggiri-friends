package com.friends.ggiriggiri.ui.second.group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentGroupBinding
import com.friends.ggiriggiri.ui.adapter.GroupPagerAdapter
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.google.android.material.tabs.TabLayoutMediator


class GroupFragment : Fragment() {

    lateinit var fragmentGroupBinding: FragmentGroupBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentGroupBinding = FragmentGroupBinding.inflate(inflater, container, false)

        settingToolbarGroup()

        settingViewPager()

        return fragmentGroupBinding.root
    }

    // Toolbar_Group
    private fun settingToolbarGroup(){
        fragmentGroupBinding.apply {
            toolbarGroup.setTitle("끼리끼리")
            toolbarGroup.setNavigationIcon(R.drawable.ic_arrow_back_ios)
            toolbarGroup.setNavigationOnClickListener {
                backCustomDialog()
            }
        }
    }

    // ViewPager2 설정
    private fun settingViewPager(){
        val adapter = GroupPagerAdapter(this)
        fragmentGroupBinding.view2.adapter = adapter

        // TabLayout과 ViewPager2 동기화
        TabLayoutMediator(fragmentGroupBinding.tabLayoutGroup, fragmentGroupBinding.view2) { tab, position ->
            tab.text = when (position) {
                0 -> "그룹 만들기"
                1 -> "그룹 들어가기"
                else -> ""
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentGroupBinding = null!!
    }

    // 뒤로가기 커스텀 다이얼로그
    private fun backCustomDialog(){
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "로그인 화면으로 이동하시겠습니까?",
            icon = R.drawable.ic_error, // 아이콘 리소스
            positiveText = "확인",
            onPositiveClick = {

            },
            negativeText = "취소",
            onNegativeClick = {

            }
        )
        dialog.showCustomDialog()
    }
}