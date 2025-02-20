package com.friends.ggiriggiri.ui.second.group

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentGroupBinding
import com.friends.ggiriggiri.ui.adapter.GroupPagerAdapter
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.first.register.UserModel
import com.google.android.material.tabs.TabLayoutMediator
import com.kakao.sdk.user.UserApiClient


class GroupFragment : Fragment() {

    private var fragmentGroupBinding: FragmentGroupBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentGroupBinding = FragmentGroupBinding.inflate(inflater, container, false)

        settingToolbarGroup()

        settingViewPager()

        return fragmentGroupBinding!!.root
    }

    // Toolbar_Group
    private fun settingToolbarGroup(){
        fragmentGroupBinding?.apply {
            toolbarGroup.setTitle("끼리끼리")
            toolbarGroup.setNavigationIcon(R.drawable.ic_arrow_back_ios)
            toolbarGroup.setNavigationOnClickListener {
                backCustomDialog()
            }
        }
    }

    // ViewPager2 설정
    private fun settingViewPager() {
        fragmentGroupBinding?.let { binding ->
            val adapter = GroupPagerAdapter(this)
            binding.view2.adapter = adapter

            TabLayoutMediator(binding.tabLayoutGroup, binding.view2) { tab, position ->
                tab.text = when (position) {
                    0 -> "그룹 만들기"
                    1 -> "그룹 들어가기"
                    else -> ""
                }
            }.attach()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        fragmentGroupBinding = null
    }

    // 뒤로가기 커스텀 다이얼로그
    fun backCustomDialog(){
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "로그인 화면으로 이동하시겠습니까?",
            icon = R.drawable.ic_error, // 아이콘 리소스
            positiveText = "확인",
            onPositiveClick = {
                logoutAndGoToLogin()
            },
            negativeText = "취소",
            onNegativeClick = {

            }
        )
        dialog.showCustomDialog()
    }

    private fun logoutAndGoToLogin() {
        val app = requireActivity().application as App

        // 로그인 정보 초기화
        app.loginUserModel = UserModel()

        // 로그아웃
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("GroupFragment", "로그아웃 실패", error)
            } else {
                Log.d("GroupFragment", "✅ 카카오 로그아웃 성공")
            }
        }

        // 로그인 화면으로 이동
        val intent = Intent(requireContext(), LoginActivity::class.java)
        // 스택 초기화ㅂ
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}