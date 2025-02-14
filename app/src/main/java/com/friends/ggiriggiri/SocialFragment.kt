package com.friends.ggiriggiri

import android.os.Bundle
import android.text.TextUtils.replace
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.databinding.FragmentSocialBinding
import com.friends.ggiriggiri.ui.fifth.memory.MemoryFragment
import com.friends.ggiriggiri.ui.third.home.HomeFragment


class SocialFragment : Fragment() {

    private lateinit var fragmentSocialBinding: FragmentSocialBinding

    lateinit var socialActivity: SocialActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSocialBinding = FragmentSocialBinding.inflate(layoutInflater)

        socialActivity = activity as SocialActivity

        if (savedInstanceState == null) {
            fragmentSocialBinding.bottomNavigationViewSocial.selectedItemId = R.id.nav_home
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentSocialSub, HomeFragment())
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
                    //navigateWithoutBackStack()
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
}

