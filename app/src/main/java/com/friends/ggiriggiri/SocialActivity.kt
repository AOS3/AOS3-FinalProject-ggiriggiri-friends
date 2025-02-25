package com.friends.ggiriggiri

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.friends.ggiriggiri.databinding.ActivitySocialBinding
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SocialActivity : AppCompatActivity() {

    private lateinit var activitySocialBinding: ActivitySocialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activitySocialBinding = ActivitySocialBinding.inflate(layoutInflater)
        setContentView(activitySocialBinding.root)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val loginUser = (application as App).loginUserModel

        Log.d("SocialActivity", "로그인한 유저 정보")
        Log.d("SocialActivity", "이메일: ${loginUser.userId}")
        Log.d("SocialActivity", "문서아이디: ${loginUser.userDocumentId}")
        Log.d("SocialActivity", "닉네임: ${loginUser.userName}")
        Log.d("SocialActivity", "프로필 사진 URL: ${loginUser.userProfileImage}")
        Log.d("SocialActivity", "가입 날짜: ${loginUser.userJoinTime}")
        Log.d("SocialActivity", "유저 상태: ${loginUser.userState.num}")
        Log.d("SocialActivity", "소셜 로그인 타입: ${loginUser.userSocialLogin.num}")
        Log.d("SocialActivity", "자동 로그인 토큰: ${loginUser.userAutoLoginToken}")
        Log.d("SocialActivity", "그룹: ${loginUser.userGroupDocumentID}")

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewSocialMain, SocialFragment())
                .commit()
        }
    }

    fun getUserDocumentId(): String? {
        val loginUser = (application as App).loginUserModel
        return loginUser.userDocumentId
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerViewSocialMain, fragment)
            .addToBackStack(null)
            .commit()
    }
}