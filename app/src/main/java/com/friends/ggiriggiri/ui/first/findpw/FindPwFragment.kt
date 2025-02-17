package com.friends.ggiriggiri.ui.first.findpw

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentFindPwBinding
import com.friends.ggiriggiri.databinding.FragmentLoginBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.first.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log

@AndroidEntryPoint
class FindPwFragment : Fragment() {
    private var _binding: FragmentFindPwBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginActivity: LoginActivity
    private val findPwViewModel: FindPwViewModel by viewModels()

    // 휴대폰 번호 유효성 결과
    var isPhoneNumberValidResult = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindPwBinding.inflate(inflater,container,false)
        loginActivity = activity as LoginActivity
        // 옵저버
        observeViewModel()

        //툴바세팅
        settingToolbar()

        //버튼 세팅
        settingButtons()

        //휴대폰번호 입력 유효성검사
        settingTilFindPwFragmentPhoneNumberNumber()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showBackPressedDialog()
            }
        })
    }

    // 옵저버
    private fun observeViewModel() {
        findPwViewModel.apply {
            //전화번호
            phoneNumberErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.tilFindPwFragmentPhoneNumber.error = errorMessage
            }

            // 휴대폰 번호 유효성 결과
            isPhoneNumberValid.observe(viewLifecycleOwner) { it ->
                isPhoneNumberValidResult = it
            }
        }
    }

    //휴대폰번호 입력 유효성검사
    private fun settingTilFindPwFragmentPhoneNumberNumber() {
        binding.etFindPwFragmentPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                findPwViewModel.etFindPwFragmentPhoneNumberWatcher(s.toString())
            }
        })
    }
    // 공통으로 사용할 뒤로가기 다이얼로그
    fun showBackPressedDialog() {
        val customDialog = CustomDialog(
            context = loginActivity,
            onPositiveClick = {
                loginActivity.removeFragment(LoginFragmentName.FIND_PW_FRAGMENT)
            },
            positiveText = "확인",
            onNegativeClick = {},
            negativeText = "취소",
            contentText = "정말 뒤로 가시겠습니까?\n로그인 화면으로 이동합니다",
            icon = R.drawable.ic_error
        )
        customDialog.showCustomDialog()
    }

    //툴바세팅
    fun settingToolbar() {
        binding.tbFindPwFragment.apply {
            title = "비밀번호 찾기"
            isTitleCentered = true
            setNavigationIcon(R.drawable.ic_arrow_back_ios)
            setNavigationOnClickListener {
                val customDialog = CustomDialog(
                    context = loginActivity,
                    onPositiveClick = {
                        loginActivity.removeFragment(LoginFragmentName.FIND_PW_FRAGMENT)
                    },
                    positiveText = "확인",
                    onNegativeClick = {},
                    negativeText = "취소",
                    contentText = "정말 뒤로 가시겠습니까?\n로그인 화면으로 이동합니다",
                    icon = R.drawable.ic_error
                )

                customDialog.showCustomDialog()
            }
        }
    }

    //버튼 세팅
    fun settingButtons() {
        binding.apply {
            // 비빌번호 재설정하기
            btnFindPwFragmentResetPw.setOnClickListener {
                loginActivity.replaceFragment(LoginFragmentName.RESET_PW_FRAGMENT,true,true,null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}