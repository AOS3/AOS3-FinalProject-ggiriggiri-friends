package com.friends.ggiriggiri.ui.first.login

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.GroupActivity
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import com.friends.ggiriggiri.ui.first.register.UserService
import com.friends.ggiriggiri.util.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val service: UserService
): ViewModel() {

    // 아이디 에러 메시지
    val idErrorMessage = MutableLiveData<String?>()

    // 비밀번호 에러 메시지
    val pwErrorMessage = MutableLiveData<String?>()

    // 아이디, 비밀번호 입력확인
    var isValid = true

    // 로그인ㄱㄱ
    var isLoginSuccess = MutableLiveData(false)

    // 로그인 버튼 클릭
    fun btnLoginFragmentLoginOnClick(id: String, pw: String) {

        isValid = true

        // 아이디 유효성 검사
        if (id.isBlank()) {
            idErrorMessage.value = "아이디를 입력해 주세요."
            isValid = false
        } else {
            idErrorMessage.value = null
        }
        // 비밀번호 유효성 검사
        if (pw.isBlank()) {
            pwErrorMessage.value = "비밀번호를 입력해 주세요."
            isValid = false
        } else {
            pwErrorMessage.value = null
        }

        // 모두ok면 아이디찾기진행
        if(isValid){
            isLoginSuccess.value = true
            Log.d("isLoginSuccess","${isLoginSuccess.value}")
        }

    }

    fun loginUser(loginActivity: LoginActivity, id: String, pw: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val progressDialog = withContext(Dispatchers.Main) {
                CustomDialogProgressbar(loginActivity).apply { show() }
            }

            try {
                val user = service.login(id, pw)
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if (user != null) {
                        if (user.userState == UserState.WITHDRAW) {
                            Toast.makeText(loginActivity, "탈퇴한 회원입니다", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(loginActivity, "로그인 완료!", Toast.LENGTH_LONG).show()
                            val app = loginActivity.application as App
                            app.loginUserModel = user
                            val intent = Intent(loginActivity, GroupActivity::class.java)
                            loginActivity.startActivity(intent)
                            loginActivity.finish()
                        }
                    } else {
                        Toast.makeText(loginActivity, "아이디 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Log.e("로그인 실패", "로그인 실패: ${e.message}")
                }
            }
        }
    }
}

