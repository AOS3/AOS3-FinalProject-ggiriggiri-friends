package com.friends.ggiriggiri.ui.start.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentPrivacyPolicyBinding
import com.friends.ggiriggiri.databinding.FragmentRegisterBinding

class PrivacyPolicyFragment : Fragment() {
    private var _binding: FragmentPrivacyPolicyBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginActivity: LoginActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
        loginActivity = activity as LoginActivity

        binding.wbPrivacyPolicy.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl("https://sites.google.com/view/ggiriggiri-privacy-policy")
        }

        //툴바세팅
        binding.tbPrivacyPolicyFragment.apply {
            title = "개인정보처리방침"
            isTitleCentered = true
            setNavigationIcon(R.drawable.ic_arrow_back_ios)
            setNavigationOnClickListener {
                loginActivity.removeFragment(LoginFragmentName.PRIVACY_POLICY_FRAGMENT)
            }
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}