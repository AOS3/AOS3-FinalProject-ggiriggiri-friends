package com.friends.ggiriggiri.ui.start.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.GroupActivity
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.data.service.KakaoLoginService
import com.friends.ggiriggiri.data.service.NaverLoginService
import com.friends.ggiriggiri.databinding.FragmentLoginBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import com.friends.ggiriggiri.ui.custom.CustomLoginDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLDecoder
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginActivity: LoginActivity
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var progressDialog: CustomDialogProgressbar

    @Inject
    lateinit var kakaoLoginService: KakaoLoginService
    @Inject
    lateinit var naverLoginService: NaverLoginService

    //true면 로그인 진행가능
    private var isLoginSuccessResult: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        loginActivity = activity as LoginActivity

        // 다이얼로그 초기화
        progressDialog = CustomDialogProgressbar(requireContext())

        // ViewModel observe
        observeViewModel()

        // 버튼 리스너
        settingButtons()

        // 카카오 초기화
        KakaoSdk.init(requireActivity(), getString(R.string.kakao_native_app_key))

        // 네이버 SDK 초기화
        NaverIdLoginSDK.initialize(
            loginActivity,
            getString(R.string.OAUTH_CLIENT_ID),
            getString(R.string.OAUTH_CLIENT_SECRET),
            getString(R.string.OAUTH_CLIENT_NAME)
        )

        Log.d("NaverLogin", "Naver Client ID: ${NaverIdLoginSDK}")


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

            user_google.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    (requireActivity().application as App).loginUserModel = user
                    navigateToGroupActivity()
                }
            }

        }
    }

    private fun navigateToGroupActivity() {
        val intent = Intent(requireContext(), GroupActivity::class.java)
        startActivity(intent)
        progressDialog.dismiss()
        requireActivity().finish()
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

    private fun socialLoginButtons() {
        binding.apply {
            // 카카오 로그인
            ivLoginFragmentKakaoLogin.setOnClickListener {
                val dialog = CustomLoginDialog(
                    context = requireContext(),
                    onPositiveClick = { loginWithKakao() },
                    positiveText = "네",
                    onNegativeClick = {},
                    negativeText = "취소",
                    contentText = "개인정보처리방침을 확인하셨나요?",
                    icon = R.drawable.ic_check_circle,
                    onViewPrivacyPolicy = {
                        loginActivity.replaceFragment(LoginFragmentName.PRIVACY_POLICY_FRAGMENT,true,true,null)
                    }
                )
                dialog.showCustomDialog()

            }
            // 네이버 로그인
            ivLoginFragmentNaverLogin.setOnClickListener {
                val dialog = CustomLoginDialog(
                    context = requireContext(),
                    onPositiveClick = { loginWithNaver() },
                    positiveText = "네",
                    onNegativeClick = {},
                    negativeText = "취소",
                    contentText = "개인정보처리방침을 확인하셨나요?",
                    icon = R.drawable.ic_check_circle,
                    onViewPrivacyPolicy = {
                        loginActivity.replaceFragment(LoginFragmentName.PRIVACY_POLICY_FRAGMENT,true,true,null)
                    }
                )
                dialog.showCustomDialog()
            }
            // 구글 로그인
            ivLoginFragmentGoogleLogin.setOnClickListener {
                val dialog = CustomLoginDialog(
                    context = requireContext(),
                    onPositiveClick = { requestGoogleLogin() },
                    positiveText = "네",
                    onNegativeClick = {},
                    negativeText = "취소",
                    contentText = "개인정보처리방침을 확인하셨나요?",
                    icon = R.drawable.ic_check_circle,
                    onViewPrivacyPolicy = {
                        loginActivity.replaceFragment(LoginFragmentName.PRIVACY_POLICY_FRAGMENT,true,true,null)
                    }
                )
                dialog.showCustomDialog()
            }

        }
    }

    private fun loginWithNaver() {
        NaverIdLoginSDK.authenticate(loginActivity, object : OAuthLoginCallback {
            override fun onSuccess() {
                Log.d("NaverLogin", "네이버 로그인 성공")
                val accessToken = NaverIdLoginSDK.getAccessToken()
                Log.d("NaverLogin", "Access Token: $accessToken")

                fetchNaverUserInfo()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Log.e("NaverLogin", "네이버 로그인 실패: $message")
            }

            override fun onError(errorCode: Int, message: String) {
                Log.e("NaverLogin", "네이버 로그인 오류: $message")
            }
        })
    }

    private fun loginWithKakao() {
        progressDialog.show()

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
            progressDialog.dismiss()
        } else if (token != null) {
            Log.d("KakaoLogin", "로그인 성공 ${token.accessToken}")


            fetchKakaoUserInfo()
        }
    }

    private fun fetchKakaoUserInfo() {
        lifecycleScope.launch {
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e("KakaoUserInfo", "사용자 정보 요청 실패", error)
                    progressDialog.dismiss()
                    return@me
                }

                if (user == null) {
                    Log.e("KakaoUserInfo", "사용자 정보가 null입니다.")
                    progressDialog.dismiss()
                    return@me
                }

                Log.d("KakaoUserInfo", "사용자 전체 정보: $user")

                val email = user.kakaoAccount?.email ?: "이메일 없음"
                val nickname = user.kakaoAccount?.profile?.nickname ?: "닉네임 없음"
                val profileImage = user.kakaoAccount?.profile?.thumbnailImageUrl ?: ""

                lifecycleScope.launch {
                    val cancelMembershipCheckResult = async {
                        kakaoLoginService.userCancelMembershipCheck(email)
                    }.await()

                    if (!cancelMembershipCheckResult) {
                        progressDialog.dismiss()
                        Toast.makeText(loginActivity, "탈퇴한 회원입니다", Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    // OAuthToken 가져오기
                    UserApiClient.instance.accessTokenInfo { tokenInfo, tokenError ->
                        if (tokenError != null) {
                            Log.e("KakaoToken", "OAuthToken 가져오기 실패", tokenError)
                            progressDialog.dismiss()
                            return@accessTokenInfo
                        }

                        val kakaoToken = tokenInfo?.id.toString() // 카카오 OAuthToken 값 사용

                        // val kakaoLoginService = KakaoLoginService(KakaoLoginRepository())
                        lifecycleScope.launch(Dispatchers.IO) {
                            val userModel = kakaoLoginService.handleKakaoLogin(
                                email,
                                nickname,
                                profileImage,
                                kakaoToken
                            )

                            withContext(Dispatchers.Main) {
                                if (userModel != null) {
                                    progressDialog.dismiss()
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
    }

    private fun fetchNaverUserInfo() {
        progressDialog.show()
        val accessToken = NaverIdLoginSDK.getAccessToken() ?: return
        val url = "https://openapi.naver.com/v1/nid/me"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Authorization", "Bearer $accessToken")

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val responseStream =
                        connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("NaverLogin", "네이버 사용자 정보: $responseStream")

                    // JSON 파싱
                    val jsonObject = JSONObject(responseStream)
                    val response = jsonObject.getJSONObject("response")

                    val userId = response.getString("id") // 네이버 고유 ID
                    val email = response.optString("email", "이메일 없음")
                    val rawName = response.optString("name", "이름 없음")
                    val nickname = response.optString("nickname", "닉네임 없음")
                    val profileImage = response.optString("profile_image", "")

                    // 유니코드(이스케이프된 문자) 디코딩
                    val name = URLDecoder.decode(rawName, "UTF-8")

                    // val naverLoginService = NaverLoginService(NaverLoginRepository())
                    lifecycleScope.launch(Dispatchers.Main) {
                        val userModel = naverLoginService.handleNaverLogin(
                            email,
                            name,
                            profileImage,
                            userId
                        )
                        val cancelMembershipCheckResult = async {
                            naverLoginService.userCancelMembershipCheck(email)
                        }.await()

                        if (!cancelMembershipCheckResult) {
                            progressDialog.dismiss()
                            Toast.makeText(loginActivity, "탈퇴한 회원입니다", Toast.LENGTH_LONG).show()
                            return@launch
                        }
                        withContext(Dispatchers.Main) {
                            if (userModel != null) {
                                progressDialog.dismiss()
                                Log.d("NaverUserInfo", "Firestore에서 가져온 유저: ${userModel.userId}")

                                // App 클래스에 로그인 유저 정보 저장
                                (loginActivity.application as App).loginUserModel = userModel

                                // GroupActivity로 이동
                                val intent = Intent(
                                    requireContext(),
                                    GroupActivity::class.java
                                )
                                startActivity(intent)
                                loginActivity.finish()
                            } else {
                                Log.e("NaverUserInfo", "Firestore 유저 정보를 가져오는 데 실패했음")
                            }
                        }
                    }
                } else {
                    Log.e("NaverLogin", "네이버 사용자 정보 요청 실패: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("NaverLogin", "네이버 사용자 정보 요청 중 오류 발생", e)
            }
        }
    }


    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }
    private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (idToken.isNullOrEmpty()) {
                progressDialog.dismiss()
                return@registerForActivityResult
            }
            progressDialog.show()

            lifecycleScope.launch {
                val cancelMembershipCheckResult = async {
                    naverLoginService.userCancelMembershipCheck(account.email!!)
                }.await()

                if (!cancelMembershipCheckResult) {
                    progressDialog.dismiss()
                    Toast.makeText(loginActivity, "탈퇴한 회원입니다", Toast.LENGTH_LONG).show()
                    return@launch
                    progressDialog.dismiss()
                }
                loginViewModel.requestGiverSignIn(
                    idToken = idToken,
                    email = account.email ?: "",
                    userName = account.displayName ?: "",
                    profileImage = account.photoUrl?.toString() ?: ""
                )
            }


        } catch (e: ApiException) {
            Log.e("GoogleLogin", "Google 로그인 실패: ${e.statusCode}", e)
            progressDialog.dismiss()
        }
    }



    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_client_id)) // ID 토큰 요청
            .requestEmail() // 이메일 요청
            .build()

        return GoogleSignIn.getClient(requireActivity(), googleSignInOption)
    }

    private fun requestGoogleLogin() {
        progressDialog.show()
        val signInIntent = googleSignInClient.signInIntent
        googleAuthLauncher.launch(signInIntent)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}