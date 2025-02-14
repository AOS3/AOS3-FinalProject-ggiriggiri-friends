package com.friends.ggiriggiri.ui.first.findpw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentFindPwBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import kotlin.math.log

class FindPwFragment : Fragment() {
    lateinit var fragmentFindPwBinding: FragmentFindPwBinding
    lateinit var loginActivity: LoginActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentFindPwBinding = FragmentFindPwBinding.inflate(inflater,container,false)
        loginActivity = activity as LoginActivity

        //툴바세팅
        settingToolbar()

        //버튼 세팅
        settingButtons()

        return fragmentFindPwBinding.root
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
        fragmentFindPwBinding.tbFindPwFragment.apply {
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
        fragmentFindPwBinding.apply {
            // 비빌번호 재설정하기
            btnFindPwFragmentResetPw.setOnClickListener {
                loginActivity.replaceFragment(LoginFragmentName.RESET_PW_FRAGMENT,true,true,null)
            }
        }
    }

}