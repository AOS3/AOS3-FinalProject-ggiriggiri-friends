package com.friends.ggiriggiri

import android.os.Bundle
import android.text.TextUtils.replace
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.databinding.FragmentSocialBinding
import com.friends.ggiriggiri.ui.fifth.memory.MemoryFragment
import com.friends.ggiriggiri.ui.fourth.mypage.MyPageFragment
import com.friends.ggiriggiri.ui.third.home.HomeFragment


class SocialFragment : Fragment() {

    private lateinit var fragmentSocialBinding: FragmentSocialBinding

    lateinit var socialActivity: SocialActivity

    // key로 사용할 상수
    companion object {
        private const val SELECTED_NAV_KEY = "selected_nav_item"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSocialBinding = FragmentSocialBinding.inflate(layoutInflater)

        socialActivity = activity as SocialActivity


        // 만약 이전 상태가 저장되어 있다면 복원하고, 없다면 기본(HomeFragment)으로 설정
        val selectedNavItem = savedInstanceState?.getInt(SELECTED_NAV_KEY) ?: R.id.nav_home
        fragmentSocialBinding.bottomNavigationViewSocial.selectedItemId = selectedNavItem


        // 현재 선택된 탭에 맞는 프래그먼트로 교체
        val startFragment = when (selectedNavItem) {
            R.id.nav_home -> HomeFragment()
            R.id.nav_memory -> MemoryFragment()
            R.id.nav_mypage -> MyPageFragment()
            else -> HomeFragment()
        }

        // 단, 이미 childFragmentManager에 프래그먼트가 있다면 교체하지 않음.
        if (childFragmentManager.findFragmentById(R.id.fragmentSocialSub) == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentSocialSub, startFragment)
                .commit()
        }


        // 하단 네비게이션 설정
        settingBottomNavigationView()


        return fragmentSocialBinding.root
    }


    // 하단 네비게이션 설정
    private fun settingBottomNavigationView() {
        fragmentSocialBinding.bottomNavigationViewSocial.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navigateWithoutBackStack(HomeFragment())
                    true
                }

                R.id.nav_memory -> {
                    navigateWithoutBackStack(MemoryFragment())
                    true
                }

                R.id.nav_mypage -> {
                    navigateWithoutBackStack(MyPageFragment())
                    true
                }

                else -> false
            }
        }
    }

    // 백스택 없이 프래그먼트 교체
    private fun navigateWithoutBackStack(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentSocialSub, fragment)
            .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SELECTED_NAV_KEY, fragmentSocialBinding.bottomNavigationViewSocial.selectedItemId)
        super.onSaveInstanceState(outState)
    }
}

