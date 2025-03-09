package com.friends.ggiriggiri.ui.settinggroup.group

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
import com.friends.ggiriggiri.ui.start.register.UserModel
import com.friends.ggiriggiri.util.UserSocialLoginState
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK


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

        // 자동 로그인 토큰 삭제
        val sharedPreferences = requireActivity().getSharedPreferences("GGiriggiriPrefs", android.content.Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("autoLoginToken").apply()

        val userSocialLogin = app.loginUserModel.userSocialLogin

        when (userSocialLogin) {
            UserSocialLoginState.KAKAO -> {
                // ✅ 카카오 로그아웃
                try {
                    KakaoSdk.init(requireContext(), getString(R.string.kakao_native_app_key)) // 카카오 SDK 초기화
                    UserApiClient.instance.logout { error ->
                        if (error != null) {
                            Log.e("GroupFragment", "카카오 로그아웃 실패", error)
                        } else {
                            Log.d("GroupFragment", "✅ 카카오 로그아웃 성공")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("GroupFragment", "카카오 로그아웃 중 오류 발생", e)
                }
            }

            UserSocialLoginState.GOOGLE -> {
                // ✅ 구글 로그아웃
                FirebaseAuth.getInstance().signOut()
                Log.d("GroupFragment", "✅ 구글 로그아웃 성공")
            }

            UserSocialLoginState.NAVER -> {
                // ✅ 네이버 로그아웃
                NaverIdLoginSDK.logout()
                Log.d("GroupFragment", "✅ 네이버 로그아웃 성공")
            }

            else -> {
                Log.d("GroupFragment", "❌ 소셜 로그인 정보를 찾을 수 없음")
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