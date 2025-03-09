package com.friends.ggiriggiri.ui.start.findpw

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import com.friends.ggiriggiri.ui.start.register.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ResetPwViewModel @Inject constructor(
    private val service: UserService
) : ViewModel() {
    // 비밀번호1
    var pw1String: String = ""

    // 비밀번호2
    var pw2String: String = ""

    // 비밀번호가 서로같음
    var isPwValid = MutableLiveData(false)

    // 비밀번호1 에러메세지
    var pw1ErrorMessage = MutableLiveData<String?>()

    // 비밀번호2 에러메세지
    var pw2ErrorMessage = MutableLiveData<String?>()

    fun etResetPwFragmentNewPw1Watcher(pw1: String) {
        pw1String = pw1
        if (pw1.isEmpty()) {
            pw1ErrorMessage.value = "비밀번호를 입력해 주세요."
            isPw12Equal()
        } else if (pw1.length < 4 || pw1.length > 20) {
            pw1ErrorMessage.value = "비밀번호는 4자 이상 20자 이하로 입력해야 합니다."
            isPw12Equal()
        } else {
            pw1ErrorMessage.value = null
            isPw12Equal()
        }
    }

    fun etResetPwFragmentNewPw2Watcher(pw2: String) {
        pw2String = pw2
        if (pw2.isEmpty()) {
            pw2ErrorMessage.value = "비밀번호를 입력해 주세요."
            isPw12Equal()
        } else if (pw2.length < 4 || pw2.length > 20) {
            pw2ErrorMessage.value = "비밀번호는 4자 이상 20자 이하로 입력해야 합니다."
            isPw12Equal()
        } else {
            pw2ErrorMessage.value = null
            isPw12Equal()
        }
    }

    // 비밀번호가 유효성검사를 통과하고 동일한지
    fun isPw12Equal() {
        isPwValid.value = false
        Log.d("test", isPwValid.toString())
        if (pw1ErrorMessage.value == null && pw2ErrorMessage.value == null && pw1String == pw2String) {
            isPwValid.value = true
        }
    }

    // 비밀번호가 유효성 검사를 통과해서 에러가없고 둘이 같을때 저장한다
    fun resetPw(loginActivity: LoginActivity, userDocumentId: String, pw: String) {
        viewModelScope.launch {
            val progressDialog = withContext(Dispatchers.Main) {
                CustomDialogProgressbar(loginActivity).apply { show() }
            }
            try {
                service.resetUserPw(userDocumentId, pw)
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                }
                Toast.makeText(loginActivity, "비밀번호가 변경되었습니다!", Toast.LENGTH_LONG).show()
                loginActivity.removeFragment(LoginFragmentName.FIND_PW_FRAGMENT)
                loginActivity.removeFragment(LoginFragmentName.RESET_PW_FRAGMENT)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(loginActivity, "오류가 발생했습니다: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        Log.d("test", isPwValid.value.toString())
    }

}