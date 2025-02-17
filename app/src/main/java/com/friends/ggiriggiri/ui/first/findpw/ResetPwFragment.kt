package com.friends.ggiriggiri.ui.first.findpw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentLoginBinding
import com.friends.ggiriggiri.databinding.FragmentResetPwBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.first.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log

@AndroidEntryPoint
class ResetPwFragment : Fragment() {
    private var _binding: FragmentResetPwBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginActivity: LoginActivity
    private val resetPwViewModel: ResetPwViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPwBinding.inflate(inflater,container,false)
        loginActivity = activity as LoginActivity

        //툴바세팅
        settingToolbar()

        //버튼 세팅
        settingButtons()

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
    // 공통으로 사용할 뒤로가기 다이얼로그
    fun showBackPressedDialog() {
        val customDialog = CustomDialog(
            context = loginActivity,
            onPositiveClick = {
                loginActivity.removeFragment(LoginFragmentName.FIND_PW_FRAGMENT)
                loginActivity.removeFragment(LoginFragmentName.RESET_PW_FRAGMENT)
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
        binding.tbResetPwFragment.apply {
            title = "비밀번호 재설정"
            isTitleCentered = true
            setNavigationIcon(R.drawable.ic_arrow_back_ios)
            setNavigationOnClickListener {
                val customDialog = CustomDialog(
                    context = loginActivity,
                    onPositiveClick = {
                        loginActivity.removeFragment(LoginFragmentName.FIND_PW_FRAGMENT)
                        loginActivity.removeFragment(LoginFragmentName.RESET_PW_FRAGMENT)
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
            // 비빌번호 재설정 완료
            btnResetPwFragmentResetPw.setOnClickListener {
                val customDialog = CustomDialog(
                    context = loginActivity,
                    onPositiveClick = {
                        loginActivity.removeFragment(LoginFragmentName.FIND_PW_FRAGMENT)
                        loginActivity.removeFragment(LoginFragmentName.RESET_PW_FRAGMENT)
                    },
                    positiveText = "변경하기",
                    onNegativeClick = {},
                    negativeText = "취소",
                    contentText = "비밀번호를 재설정 하시겠습니까?\n로그인 화면으로 이동합니다",
                    icon = R.drawable.ic_edit
                )

                customDialog.showCustomDialog()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}