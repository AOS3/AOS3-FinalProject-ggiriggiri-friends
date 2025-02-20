package com.friends.ggiriggiri.ui.fourth.modifyuserpw

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ModifyUserPwViewModel @Inject constructor(

) : ViewModel() {


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

    // 새 비밀번호 유효성 검사
    fun validateNewPw(){
        val pw = newPw.value ?: ""
        when {
            pw.isEmpty() -> newPwErrorMessage.value = "비밀번호를 입력해 주세요."
            pw.length < 4 || pw.length > 20 -> newPwErrorMessage.value = "비밀번호는 4자 이상 20자 이하로 입력해야 합니다."
            else -> newPwErrorMessage.value = null
        }
        validateConfirmPw() // 새 비밀번호가 바뀌면 확인도 다시 검사
        updateButtonState()
    }

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

    // 모든 유효성 검사 통과 여부 확인
    fun isValidationSuccessful() : Boolean{
        validateNewPw()
        validateConfirmPw()
        return newPwErrorMessage.value == null && confirmPwErrorMessage.value == null
    }
}
