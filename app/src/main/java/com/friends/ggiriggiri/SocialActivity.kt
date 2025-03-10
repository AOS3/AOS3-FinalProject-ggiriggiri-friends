package com.friends.ggiriggiri

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.friends.ggiriggiri.databinding.ActivitySocialBinding
import com.friends.ggiriggiri.ui.notification.UserPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SocialActivity : AppCompatActivity() {

    private lateinit var activitySocialBinding: ActivitySocialBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 권한이 허용됨
            Toast.makeText(this, "알림 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            // 권한이 거부됨
            Toast.makeText(this, "알림 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

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
        // 로그인 성공 시 userDocumentID 저장
        UserPreferences.saveUserID(this, loginUser.userDocumentId)

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

        // Android 13 이상에서만 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한 요청
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

//        // android 13  버전
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//            // 권한 목록
//            val permissionList = arrayOf(
//                Manifest.permission.POST_NOTIFICATIONS
//            )
//            requestPermissions(permissionList, 0)
//        }
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
}