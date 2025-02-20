package com.friends.ggiriggiri.ui.fourth.modifygrouppw

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ModifyGroupPwViewModel @Inject constructor(

): ViewModel() {

    // 새 비밀번호
    val newPw = MutableLiveData<String?>("")
    // 비밀번호 확인
    val confirmPw = MutableLiveData<String?>("")
    // 새 비밀번호 에러 메시지
    val newPwErrorMessage = MutableLiveData<String?>()
    // 비밀번호 확인 에러 메시지
    val confirmPwErrorMessage = MutableLiveData<String?>()
    // 버튼 활성화 상태
    val isButtonEnabled = MutableLiveData<Boolean>(false)


    // 비밀번호 확인 에러 메시지
    fun validateConfirmPw(){
        val pw = newPw.value ?: ""
        val confirm = confirmPw.value ?: ""
        when {
            confirm.isEmpty() -> confirmPwErrorMessage.value = "비밀번호를 다시 입력해 주세요."
            confirm != pw -> confirmPwErrorMessage.value = "비밀번호가 일치하지 않습니다."
            else -> confirmPwErrorMessage.value = null
        }
        updateButtonState()
    }

    // 버튼 활성화 상태 업데이트
    private fun updateButtonState() {
        isButtonEnabled.value =
            newPwErrorMessage.value == null &&
                    confirmPwErrorMessage.value == null &&
                    !newPw.value.isNullOrEmpty() &&
                    !confirmPw.value.isNullOrEmpty()
    }
}