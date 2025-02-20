package com.friends.ggiriggiri.ui.fourth.modifygroupname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ModifyGroupNameViewModel @Inject constructor(

) : ViewModel() {

    // 새 그룹명
    val newGroupName = MutableLiveData<String>("")
    // 새 그룹명 에러 메시지
    val newGroupNameError = MutableLiveData<String?>()
    // 버튼 활성화 상태
    val isButtonEnabled = MutableLiveData<Boolean>(false)

    // 새 그룹명 유효성 검사
    fun validateNewName(){
        val name = newGroupName.value ?: ""
        when {
            name.isEmpty() -> newGroupNameError.value = "그룹명은 1자 이상 입력해주세요."
            else -> newGroupNameError.value = null
        }
        updateButtonState()
    }

    // 버튼 활성화 상태 업데이트
    private fun updateButtonState() {
        isButtonEnabled.value =
            newGroupNameError.value == null && !newGroupName.value.isNullOrEmpty()
    }
}