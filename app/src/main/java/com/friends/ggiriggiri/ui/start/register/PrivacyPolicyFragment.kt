package com.friends.ggiriggiri.ui.start.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.fragment.app.FragmentManager
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentPrivacyPolicyBinding
import com.friends.ggiriggiri.databinding.FragmentRegisterBinding
import kotlin.math.log

class PrivacyPolicyFragment : Fragment() {
    private var _binding: FragmentPrivacyPolicyBinding? = null
    private val binding get() = _binding!!
    private lateinit var socialActivity: SocialActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
        socialActivity = activity as SocialActivity

        binding.wbPrivacyPolicy.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl("https://sites.google.com/view/ggiriggiri-information")
        }

        //툴바세팅
        binding.tbPrivacyPolicyFragment.apply {
            title = "개인정보처리방침/이용약관"
            isTitleCentered = true
            setNavigationIcon(R.drawable.ic_arrow_back_ios)
            setNavigationOnClickListener {
                //loginActivity.showFragment(this@PrivacyPolicyFragment)
                //loginActivity.removeFragment(LoginFragmentName.PRIVACY_POLICY_FRAGMENT)
                socialActivity.supportFragmentManager.popBackStack("PrivacyPolicyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }

        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}