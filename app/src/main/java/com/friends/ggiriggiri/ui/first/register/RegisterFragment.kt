package com.friends.ggiriggiri.ui.first.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentRegisterBinding
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
}

