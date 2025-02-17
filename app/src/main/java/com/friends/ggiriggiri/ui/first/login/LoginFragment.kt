package com.friends.ggiriggiri.ui.first.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.GroupActivity
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.databinding.FragmentLoginBinding
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import com.friends.ggiriggiri.ui.first.register.UserService
import com.friends.ggiriggiri.util.UserState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginActivity: LoginActivity
    private val loginViewModel: LoginViewModel by viewModels()

    //true면 로그인 진행가능
    private var isLoginSuccessResult: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        loginActivity = activity as LoginActivity

        // ViewModel observe
        observeViewModel()

        // 버튼 리스너
        settingButtons()

        return binding.root
    }

    private fun observeViewModel() {
        loginViewModel.apply {
            idErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.tilLoginFragmentId.error = errorMessage
            }

            pwErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.tilLoginFragmentPw.error = errorMessage
            }

            isLoginSuccess.observe(viewLifecycleOwner) { isLoginSuccess ->
                isLoginSuccessResult = isLoginSuccess
                if (isLoginSuccessResult) {
                    processingLogin()
                }
            }

        }
    }

    // 로그인 처리
    fun processingLogin() {
        binding.apply {
            val id = etLoginFragmentId.text?.toString() ?: ""
            val pw = etLoginFragmentPw.text?.toString() ?: ""

            // ViewModel에 로그인 요청 전달
            loginViewModel.loginUser(loginActivity, id, pw)
        }
    }



    private fun settingButtons() {
        binding.apply {
            // 로그인 버튼 클릭
            btnLoginFragmentLogin.setOnClickListener {
                val id = etLoginFragmentId.text?.toString() ?: ""
                val pw = etLoginFragmentPw.text?.toString() ?: ""
                loginViewModel.btnLoginFragmentLoginOnClick(id, pw)
            }

            // 회원가입 버튼 클릭
            tvLoginFragmentRegister.setOnClickListener {
                loginActivity.replaceFragment(LoginFragmentName.REGISTER_FRAGMENT, true, true, null)
            }

            // 아이디찾기 버튼 클릭
            tvLoginFragmentFindId.setOnClickListener {
                loginActivity.replaceFragment(LoginFragmentName.FIND_ID_FRAGMENT, true, true, null)
            }

            // 비밀번호찾기 버튼클릭
            tvLoginFragmentFindPw.setOnClickListener {
                loginActivity.replaceFragment(LoginFragmentName.FIND_PW_FRAGMENT, true, true, null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}