package com.friends.ggiriggiri.ui.fourth.modifygrouppw

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.data.service.MyPageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyGroupPwViewModel @Inject constructor(
    val service: MyPageService
) : ViewModel() {
    // 사용자 문서 ID
    var userDocumentId: String? = null

    // 그룹 문서 ID
    var groupDocumentId: String? = null

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

    //현재 비밀번호 유효성 검사
    fun validateCurrentPw() {
        val enteredPw = newPw.value ?: "" // 사용자가 입력한 현재 비밀번호

        if (enteredPw.isEmpty()) {
            newPwErrorMessage.value = "현재 비밀번호를 입력해 주세요."
            return
        }

        groupDocumentId?.let { documentId ->
            viewModelScope.launch {
                try {
                    // Firestore에서 사용자 데이터 가져오기
                    val group = service.getGroupPw(documentId)

                    // 서버에 저장된 비밀번호와 입력된 비밀번호 비교
                    if (group != enteredPw) {
                        newPwErrorMessage.value = null
                    } else {
                        newPwErrorMessage.value = "현재 그룹 비밀번호입니다."
                    }
                } catch (e: Exception) {
                    // 네트워크 오류 또는 데이터 누락 등 예외 처리
                    newPwErrorMessage.value = "비밀번호 확인 중 오류가 발생하였습니다."
                }
                updateButtonState()
                validateConfirmPw()
            }
        } ?: run {
            newPwErrorMessage.value = "그룹 정보를 찾을 수 없습니다."
        }
    }

    // 비밀번호 확인 에러 메시지
    fun validateConfirmPw() {
        val pw = newPw.value ?: ""
        val confirm = confirmPw.value ?: ""
        when {
            pw.isEmpty() -> newPwErrorMessage.value = "비밀번호를 입력해 주세요."
            pw.length < 4 || pw.length > 20 -> newPwErrorMessage.value =
                "비밀번호는 4자 이상 20자 이하로 입력해야 합니다."

            confirm.isEmpty() -> confirmPwErrorMessage.value = "비밀번호를 입력해 주세요."
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