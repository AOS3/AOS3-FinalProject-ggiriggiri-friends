package com.friends.ggiriggiri.ui.first.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentRegisterBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import kotlin.math.log

class RegisterFragment : Fragment() {
    lateinit var fragmentRegisterBinding: FragmentRegisterBinding
    lateinit var loginActivity: LoginActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        loginActivity = activity as LoginActivity

        //툴바세팅
        settingToolbar()

        //버튼 세팅
        settingButtons()


        return fragmentRegisterBinding.root
    }

    //툴바세팅
    fun settingToolbar() {
        fragmentRegisterBinding.tbRegisterFragment.apply {
            title = "회원가입"
            isTitleCentered = true
            setNavigationIcon(R.drawable.ic_arrow_back_ios)
            setNavigationOnClickListener {
                loginActivity.removeFragment(LoginFragmentName.REGISTER_FRAGMENT)
            }
        }
    }

    //버튼 세팅
    fun settingButtons(){
        fragmentRegisterBinding.apply {
            //회원가입 완료
            btnRegisterFragmentSignup.setOnClickListener {
                val customDialog = CustomDialog(
                    context = loginActivity, // Activity 또는 Fragment의 context
                    onPositiveClick = {
                        loginActivity.removeFragment(LoginFragmentName.REGISTER_FRAGMENT)
                        Toast.makeText(loginActivity, "가입 완료", Toast.LENGTH_SHORT).show()
                    },
                    positiveText = "확인",
                    onNegativeClick = {

                    },
                    negativeText = "취소",
                    contentText = "가입하시겠습니까?",
                    icon = R.drawable.ic_check_circle // 아이콘 리소스
                )
                customDialog.showCustomDialog()
            }

            //아이디 중복확인
            btnRegisterFragmentDuplicationCheck.setOnClickListener {

            }

            //인증번호 요청
            btnRegisterFragmentGetCertificationNumber.setOnClickListener {

            }

            //인증번호 확인
            btnRegisterFragmentConfirmCertificationNumber.setOnClickListener {

            }
        }
    }
}

