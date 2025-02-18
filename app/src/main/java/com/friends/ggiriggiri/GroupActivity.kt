package com.friends.ggiriggiri

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.friends.ggiriggiri.databinding.ActivityGroupBinding
import com.friends.ggiriggiri.ui.second.group.GroupFragment

class GroupActivity : AppCompatActivity() {

    private lateinit var activityGroupBinding: ActivityGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activityGroupBinding =ActivityGroupBinding.inflate(layoutInflater)
        setContentView(activityGroupBinding.root)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // ✅ App에서 로그인한 유저 정보 가져오기
        val loginUser = (application as App).loginUserModel

        Log.d("GroupActivity", "로그인한 유저 정보")
        Log.d("GroupActivity", "이메일: ${loginUser.userId}")
        Log.d("GroupActivity", "닉네임: ${loginUser.userName}")
        Log.d("GroupActivity", "프로필 사진 URL: ${loginUser.userProfileImage}")
        Log.d("GroupActivity", "가입 날짜: ${loginUser.userJoinTime}")
        Log.d("GroupActivity", "유저 상태: ${loginUser.userState.num}")
        Log.d("GroupActivity", "소셜 로그인 타입: ${loginUser.userSocialLogin.num}")
        Log.d("GroupActivity", "자동 로그인 토큰: ${loginUser.userAutoLoginToken}")

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentGroupActivity, GroupFragment())
                .commit()
        }
    }
}