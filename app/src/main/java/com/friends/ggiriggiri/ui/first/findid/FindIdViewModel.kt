package com.friends.ggiriggiri.ui.first.findid

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.GroupActivity
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import com.friends.ggiriggiri.ui.first.register.UserService
import com.friends.ggiriggiri.util.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FindIdViewModel @Inject constructor(
    private val service: UserService
) : ViewModel() {

    // 이름 에러 메시지
    val nameErrorMessage = MutableLiveData<String?>()

    // 전화번호 에러 메시지
    val phoneNumberErrorMessage = MutableLiveData<String?>()

    // 이름, 전화번호 입력확인
    var isValid = true

    // 아이디찾기ㄱㄱ
    var canWeFindId = MutableLiveData(false)

    // 아이디찾기 버튼 클릭
    fun btnFindIdFragmentFindIdOnClick(name: String, phoneNumber: String) {

        isValid = true

        // 이름 유효성 검사
        if (name.isBlank()) {
            nameErrorMessage.value = "이름을 입력해 주세요."
            isValid = false
        } else {
            nameErrorMessage.value = null
        }
        // 전화번호 유효성 검사
        if (phoneNumber.isBlank()) {
            phoneNumberErrorMessage.value = "전화번호를 입력해 주세요."
            isValid = false
        } else {
            phoneNumberErrorMessage.value = null
        }

        // 모두ok면 아이디찾기진행
        if(isValid){
            canWeFindId.value = true
            Log.d("canWeFindIdResult","canWeFindIdResult")
        }

    }

    fun findId(loginActivity: LoginActivity, name: String, phoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val progressDialog = withContext(Dispatchers.Main) {
                CustomDialogProgressbar(loginActivity).apply { show() }
            }

            try {
                val userId = service.findId(name, phoneNumber)
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if (userId.isBlank()) {
                        Toast.makeText(loginActivity, "이름 또는 전화번호가 잘못되었습니다.", Toast.LENGTH_LONG).show()
                    } else {
                        val customDialog = CustomDialog(
                            context = loginActivity,
                            onPositiveClick = {
                                loginActivity.removeFragment(LoginFragmentName.FIND_ID_FRAGMENT)
                            },
                            positiveText = "로그인",
                            onNegativeClick = {
                                loginActivity.replaceFragment(
                                    LoginFragmentName.FIND_PW_FRAGMENT,
                                    false,
                                    true,
                                    null
                                )
                            },
                            negativeText = "비밀번호찾기",
                            contentText = "$name 님의 아이디는 $userId 입니다.",
                            icon = R.drawable.ic_check_circle
                        )
                        customDialog.showCustomDialog()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Log.e("아이디 찾기 실패", "에러: ${e.message}")
                }
            }
        }
    }



}