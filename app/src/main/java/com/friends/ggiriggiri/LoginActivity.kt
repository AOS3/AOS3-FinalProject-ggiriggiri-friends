package com.friends.ggiriggiri

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.friends.ggiriggiri.databinding.ActivityLoginBinding
import com.friends.ggiriggiri.ui.first.findid.FindIdFragment
import com.friends.ggiriggiri.ui.first.findpw.FindPwFragment
import com.friends.ggiriggiri.ui.first.findpw.ResetPwFragment
import com.friends.ggiriggiri.ui.first.login.LoginFragment
import com.friends.ggiriggiri.ui.first.register.RegisterFragment
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var activityLoginBinding: ActivityLoginBinding

    // 다음 Fragment, 현재 Fragment
    var newFragment: Fragment? = null
    var oldFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)


        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //초기화면 세팅
        supportFragmentManager.commit {
            replace(R.id.fcvLoginActivity, LoginFragment())
        }
    }

    // 프래그먼트 교체 함수
    fun replaceFragment(loginFragmentName: LoginFragmentName, isAddToBackStack:Boolean, animate:Boolean, dataBundle: Bundle?){
        // newFragment가 null이 아니라면 oldFragment 변수에 담아준다.
        if(newFragment != null){
            oldFragment = newFragment
        }
        // 프래그먼트 객체
        newFragment = when(loginFragmentName){
            LoginFragmentName.LOGIN_FRAGMENT -> LoginFragment()
            LoginFragmentName.REGISTER_FRAGMENT -> RegisterFragment()
            LoginFragmentName.FIND_ID_FRAGMENT -> FindIdFragment()
            LoginFragmentName.FIND_PW_FRAGMENT -> FindPwFragment()
            LoginFragmentName.RESET_PW_FRAGMENT -> ResetPwFragment()
        }

        // bundle 객체가 null이 아니라면
        if(dataBundle != null){
            newFragment?.arguments = dataBundle
        }

        // 프래그먼트 교체
        supportFragmentManager.commit {

            if(animate) {
                // 만약 이전 프래그먼트가 있다면
                if(oldFragment != null){
                    oldFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                    oldFragment?.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
                }

                newFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                newFragment?.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
                newFragment?.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                newFragment?.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
            }

            replace(R.id.fcvLoginActivity, newFragment!!)
            if(isAddToBackStack){
                addToBackStack(loginFragmentName.str)
            }
        }
    }


    // 프래그먼트를 BackStack에서 제거하는 메서드
    fun removeFragment(fragmentName: LoginFragmentName){
        supportFragmentManager.popBackStack(fragmentName.str, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}

// 프래그먼트들을 나타내는 값들
enum class LoginFragmentName(var number:Int, var str:String){
    // 로그인 화면
    LOGIN_FRAGMENT(1, "LoginFragment"),
    // 회원 가입 화면
    REGISTER_FRAGMENT(2, "RegisterStep1Fragment"),
    // 아이디 찾기
    FIND_ID_FRAGMENT(3,"FindIdFragment"),
    // 비밀번호 찾기
    FIND_PW_FRAGMENT(4,"FindPwFragment"),
    // 비밀번호 재설정
    RESET_PW_FRAGMENT(5,"ResetPwFragment")
}