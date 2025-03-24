package com.friends.ggiriggiri.ui.start.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.activity.addCallback
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentPrivacyPolicyBinding
import com.friends.ggiriggiri.databinding.FragmentRegisterBinding
import kotlin.math.log

class TermsOfUseFragment : Fragment() {
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
            loadUrl("https://sites.google.com/view/ggiriggiri-terms-of-use")
        }

        //툴바세팅
        binding.tbPrivacyPolicyFragment.apply {
            title = "이용약관"
            isTitleCentered = true
            setNavigationIcon(R.drawable.ic_arrow_back_ios)
            setNavigationOnClickListener {
                //loginActivity.showFragment(this@TermsOfUseFragment)
                loginActivity.removeFragment(LoginFragmentName.TERMS_OF_USE)
            }

        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}