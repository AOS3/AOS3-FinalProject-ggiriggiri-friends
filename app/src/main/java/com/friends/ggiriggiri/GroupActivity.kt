package com.friends.ggiriggiri

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.friends.ggiriggiri.databinding.ActivityGroupBinding
import com.friends.ggiriggiri.ui.notification.UserPreferences
import com.friends.ggiriggiri.ui.settinggroup.group.GroupFragment
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupActivity : AppCompatActivity() {

    private lateinit var activityGroupBinding: ActivityGroupBinding
    private val firestore = FirebaseFirestore.getInstance()

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

        // 로그인 성공 시 userDocumentID 저장
        UserPreferences.saveUserID(this, loginUser.userDocumentId)


        Log.d("GroupActivity", "로그인한 유저 정보")
        Log.d("GroupActivity", "이메일: ${loginUser.userId}")
        Log.d("GroupActivity", "닉네임: ${loginUser.userName}")
        Log.d("GroupActivity", "프로필 사진 URL: ${loginUser.userProfileImage}")
        Log.d("GroupActivity", "가입 날짜: ${loginUser.userJoinTime}")
        Log.d("GroupActivity", "유저 상태: ${loginUser.userState.num}")
        Log.d("GroupActivity", "소셜 로그인 타입: ${loginUser.userSocialLogin.num}")
        Log.d("GroupActivity", "자동 로그인 토큰: ${loginUser.userAutoLoginToken}")

        getUserGroupDocumentID(loginUser.userId)
    }

    private fun getUserGroupDocumentID(userEmail: String) {
        firestore.collection("UserData").whereEqualTo("userId", userEmail)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDoc = querySnapshot.documents[0]
                    val userGroupDocumentID = userDoc.getString("userGroupDocumentID") ?: ""

                    Log.d("GroupActivity", "소속된 그룹: $userGroupDocumentID")

                    if (userGroupDocumentID.isNotEmpty()) {
                        navigateToSocialActivity()
                    } else {
                        loadGroupFragment()
                    }
                } else {
                    loadGroupFragment()
                }
            }
            .addOnFailureListener { e ->
                Log.e("GroupActivity", "Firestore에서 userGroupDocumentID 가져오기 실패: ${e.message}")
                loadGroupFragment()
            }
    }

    // 소속 그룹이 있어서 소셜 화면으로 이동
    private fun navigateToSocialActivity() {
        Log.d("GroupActivity", "✅ 이미 그룹에 소속됨 → SocialActivity로 이동!")
        val intent = Intent(this, SocialActivity::class.java)
        startActivity(intent)
        finish()
    }

    // 소속 그룹이 없어서 그룹 생성/참가 화면으로 이동
    private fun loadGroupFragment() {
        if (supportFragmentManager.findFragmentById(R.id.fragmentGroupActivity) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentGroupActivity, GroupFragment())
                .commit()
        }
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