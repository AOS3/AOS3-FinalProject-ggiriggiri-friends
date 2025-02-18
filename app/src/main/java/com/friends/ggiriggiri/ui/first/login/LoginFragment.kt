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
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.data.repository.KakaoLoginRepository
import com.friends.ggiriggiri.data.service.KakaoLoginService
import com.friends.ggiriggiri.databinding.FragmentLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
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

        // 카카오 초기화
        KakaoSdk.init(requireActivity(), getString(R.string.kakao_native_app_key))

        // 소셜 로그인 버튼들
        socialLoginButtons()

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

    private fun socialLoginButtons(){
        binding.apply {
            // 카카오 로그인
            ivLoginFragmentKakaoLogin.setOnClickListener {
                loginWithKakao()
            }
            // 네이버 로그인
            ivLoginFragmentNaverLogin.setOnClickListener {

            }
            // 구글 로그인
            ivLoginFragmentGoogleLogin.setOnClickListener {

            }
        }
    }

    private fun loginWithKakao() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireActivity())) {
            UserApiClient.instance.loginWithKakaoTalk(requireActivity()) { token, error ->
                if (error != null) {
                    Log.e("KakaoLogin", "카카오톡 로그인 실패, 계정 로그인 시도", error)
                    // 카카오톡 로그인 실패 시, 카카오 계정 로그인 실행
                    UserApiClient.instance.loginWithKakaoAccount(requireActivity()) { token, error ->
                        handleLoginResult(token, error)
                    }
                } else {
                    handleLoginResult(token, error)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireActivity()) { token, error ->
                handleLoginResult(token, error)
            }
        }
    }

    private fun handleLoginResult(token: OAuthToken?, error: Throwable?) {
        if (error != null) {
            Log.e("KakaoLogin", "로그인 실패", error)
        } else if (token != null) {
            Log.d("KakaoLogin", "로그인 성공 ${token.accessToken}")

            // 🚀 로그인 성공 후 유저 정보 가져오기
            fetchUserInfo()
        }
    }

    private fun fetchUserInfo() {
        lifecycleScope.launch {
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e("KakaoUserInfo", "사용자 정보 요청 실패", error)
                    return@me
                }

                if (user == null) {
                    Log.e("KakaoUserInfo", "사용자 정보가 null입니다.")
                    return@me
                }

                Log.d("KakaoUserInfo", "사용자 전체 정보: $user")

                val email = user.kakaoAccount?.email ?: "이메일 없음"
                val nickname = user.kakaoAccount?.profile?.nickname ?: "닉네임 없음"
                val profileImage = user.kakaoAccount?.profile?.thumbnailImageUrl ?: ""

                // OAuthToken 가져오기
                UserApiClient.instance.accessTokenInfo { tokenInfo, tokenError ->
                    if (tokenError != null) {
                        Log.e("KakaoToken", "OAuthToken 가져오기 실패", tokenError)
                        return@accessTokenInfo
                    }

                    val kakaoToken = tokenInfo?.id.toString() // 카카오 OAuthToken 값 사용

                    val kakaoLoginService = KakaoLoginService(KakaoLoginRepository())
                    lifecycleScope.launch(Dispatchers.IO) {
                        val userModel = kakaoLoginService.handleKakaoLogin(email, nickname, profileImage, kakaoToken)

                        withContext(Dispatchers.Main) {
                            if (userModel != null) {
                                Log.d("KakaoUserInfo", "Firestore에서 가져온 유저: ${userModel.userId}")

                                // App 클래스에 로그인 유저 정보 저장
                                (requireActivity().application as App).loginUserModel = userModel

                                // GroupActivity로 이동
                                val intent = Intent(requireContext(), GroupActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            } else {
                                Log.e("KakaoUserInfo", "Firestore 유저 정보를 가져오는 데 실패했음")
                            }
                        }
                    }
                }
            }
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}