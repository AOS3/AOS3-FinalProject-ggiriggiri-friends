package com.friends.ggiriggiri.ui.fourth.modifygroupname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.data.service.MyPageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyGroupNameViewModel @Inject constructor(
    val service: MyPageService
) : ViewModel() {

    // 사용자 문서 ID
    var userDocumentId: String? = null
    // 그룹 문서 ID
    var groupDocumentId: String? = null
    // 그룹명
    val groupName = MutableLiveData<String?>("")
    // 그룹명 에러메시지
    val groupNameErrorMessage = MutableLiveData<String?>()
    // 버튼 활성화 상태
    val isButtonEnabled = MutableLiveData<Boolean>(false)

    // 현재 그룹명 유효성 검사
    fun validateCurrentName() {
        val name = groupName.value ?: ""

        groupDocumentId?.let { documentId ->
            viewModelScope.launch {
                try {
                    val group = service.selectGroupDataByGroupDocumentIdOne(documentId)

                    if(group.groupName == name){
                        groupNameErrorMessage.value = "현재 그룹명입니다."
                    } else {
                        groupNameErrorMessage.value = null
                    }
                } catch (e: Exception){
                    groupNameErrorMessage.value = "그룹명 확인중 오류 발생."
                }
                updateButtonState()
            }

        }?: run {
            groupNameErrorMessage.value = "그룹 정보를 찾을 수 없습니다."
        }
    }

    // 버튼 활성화 상태 업데이트
    private fun updateButtonState() {
        isButtonEnabled.value =
            groupNameErrorMessage.value == null &&
                    !groupName.value.isNullOrEmpty()
    }
}