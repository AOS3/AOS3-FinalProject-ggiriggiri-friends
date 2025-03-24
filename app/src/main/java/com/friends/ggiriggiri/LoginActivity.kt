package com.friends.ggiriggiri

import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.friends.ggiriggiri.databinding.ActivityLoginBinding
import com.friends.ggiriggiri.ui.start.findid.FindIdFragment
import com.friends.ggiriggiri.ui.start.findpw.FindPwFragment
import com.friends.ggiriggiri.ui.start.findpw.ResetPwFragment
import com.friends.ggiriggiri.ui.start.login.LoginFragment
import com.friends.ggiriggiri.ui.start.login.NotificationTestFragment
import com.friends.ggiriggiri.ui.start.register.PrivacyPolicyFragment
import com.friends.ggiriggiri.ui.start.register.RegisterFragment
import com.friends.ggiriggiri.ui.start.register.TermsOfUseFragment
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
        enableEdgeToEdge()

        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_login)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.videoViewSplashMain)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        //초기화면 세팅
        supportFragmentManager.commit { replace(R.id.fcvLoginActivity, LoginFragment()) }
        //알림화면 테스트
        //supportFragmentManager.commit { replace(R.id.fcvLoginActivity, NotificationTestFragment()) }

//        getKeyHash("12:4B:38:09:E9:51:66:AD:E1:20:99:A7:13:99:9C:60:1A:3D:13:52")
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
            LoginFragmentName.NOTIFICATION_TEST_FRAGMENT -> NotificationTestFragment()
            LoginFragmentName.PRIVACY_POLICY_FRAGMENT -> PrivacyPolicyFragment()
            LoginFragmentName.TERMS_OF_USE -> TermsOfUseFragment()
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


    fun AppCompatActivity.hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is android.widget.EditText) {
                val outRect = android.graphics.Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    hideKeyboard()
                    view.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun showFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        val currentFragment = fragmentManager.findFragmentById(R.id.fcvLoginActivity)

        if (currentFragment != null) {
            transaction.hide(currentFragment) // 현재 프래그먼트 숨기기
        }

        val existingFragment = fragmentManager.findFragmentByTag(fragment::class.java.simpleName)

        if (existingFragment == null) {
            transaction.add(R.id.fcvLoginActivity, fragment, fragment::class.java.simpleName)
        } else {
            transaction.show(existingFragment) // 기존 프래그먼트 다시 보이기
        }

        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun hideFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        if (fragment.isAdded) {
            transaction.hide(fragment)

            // 백스택에서 이전 프래그먼트 가져오기
            val previousFragment = fragmentManager.fragments.lastOrNull { it.isVisible.not() }
            if (previousFragment != null) {
                transaction.show(previousFragment) // 이전 프래그먼트 다시 보이게 하기
            }

            transaction.commit()
        }
    }



}

// 프래그먼트들을 나타내는 값들
enum class LoginFragmentName(var number:Int, var str:String) {
    // 로그인 화면
    LOGIN_FRAGMENT(1, "LoginFragment"),

    // 회원 가입 화면
    REGISTER_FRAGMENT(2, "RegisterStep1Fragment"),

    // 아이디 찾기
    FIND_ID_FRAGMENT(3, "FindIdFragment"),

    // 비밀번호 찾기
    FIND_PW_FRAGMENT(4, "FindPwFragment"),

    // 비밀번호 재설정
    RESET_PW_FRAGMENT(5, "ResetPwFragment"),

    // 메세징 테스트
    NOTIFICATION_TEST_FRAGMENT(6, "NotificationTestFragment"),

    // 개인정보 처리방침 화면
    PRIVACY_POLICY_FRAGMENT(7, "PrivacyPolicyFragment"),

    //이용약관
    TERMS_OF_USE(8,"TermsOfUseFragment")
}