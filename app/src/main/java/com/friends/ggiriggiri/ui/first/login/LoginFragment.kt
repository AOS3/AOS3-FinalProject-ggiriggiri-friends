package com.friends.ggiriggiri.ui.first.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    lateinit var fragmentLoginBinding: FragmentLoginBinding
    lateinit var loginActivity: LoginActivity

    private val loginViewModel : LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater,container,false)
        loginActivity = activity as LoginActivity

        // 버튼 리스너
        settingButtons()

        return fragmentLoginBinding.root
    }

    // 버튼 리스너
    fun settingButtons(){
        fragmentLoginBinding.apply {

            //로그인
            btnLoginFragmentLogin.setOnClickListener {

            }
            //아이디찾기
            tvLoginFragmentFindId.setOnClickListener{
                loginActivity.replaceFragment(LoginFragmentName.FIND_ID_FRAGMENT,true,true,null)
                Log.d("test","tvLoginFragmentFindId")
            }
            //비밀번호찾기
            tvLoginFragmentFindPw.setOnClickListener {
                loginActivity.replaceFragment(LoginFragmentName.FIND_PW_FRAGMENT,true,true,null)
            }
            //회원가입
            tvLoginFragmentRegister.setOnClickListener {
                loginActivity.replaceFragment(LoginFragmentName.REGISTER_FRAGMENT,true,true,null)
            }

        }
    }

}