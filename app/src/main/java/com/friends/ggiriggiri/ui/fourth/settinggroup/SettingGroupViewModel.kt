package com.friends.ggiriggiri.ui.fourth.settinggroup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.data.service.MyPageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingGroupViewModel @Inject constructor(
    val service : MyPageService
) : ViewModel() {
    // 사용자 문서 ID
    var userDocumentId: String? = null
    // 그룹 코드
    val groupCode = MutableLiveData("")

    // 그룹 코드 가져오기
    fun gettingGroupCode(){
        val documentId = userDocumentId ?: return
        viewModelScope.launch {
            try {
                val user = service.selectUserDataByUserDocumentIdOne(documentId)
                val group = service.getGroupCode(user.userGroupDocumentID)
                groupCode.value = group
            } catch (e: Exception){
                Log.e("SettingGroupViewModel", "그룹코드 가져오기 실패: ${e.message}")
            }
        }
    }

    fun exitGroup(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val documentId = userDocumentId ?: return
        viewModelScope.launch {
            try {
                service.exitGroup(documentId)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}