package com.friends.ggiriggiri.ui.settinggroup.makegroup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.data.service.MakeGroupService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MakeGroupViewModel @Inject constructor(
    private val makeGroupService: MakeGroupService
) : ViewModel() {

    var isGroupCodeAvailable = false

    fun checkGroupCode(groupCode: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val isAvailable = !makeGroupService.repository.isGroupCodeExists(groupCode)

            // UI 업데이트는 Main 스레드에서 실행
            viewModelScope.launch(Dispatchers.Main) {
                isGroupCodeAvailable = isAvailable
                onResult(isAvailable)
            }
        }
    }

    fun createGroup(userId: String, groupName: String, groupCode: String, groupPw: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val userDocumentID = makeGroupService.getUserDocumentID(userId)

            if (userDocumentID == null) {
                withContext(Dispatchers.Main) {
                    Log.e("MakeGroupViewModel", "❌ 유저 Document ID 찾기 실패")
                    onResult(false)
                }
                return@launch
            }

            val updatedGroupID = makeGroupService.createGroupAndAssignToUser(userDocumentID, groupName, groupCode, groupPw)

            withContext(Dispatchers.Main) {
                Log.d("MakeGroupViewModel", "✅ 그룹 생성 성공! Firestore에 정상 반영됨")
                onResult(true)
            }
        }
    }

    fun getUserGroupDocumentID(userId: String, onResult: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val userGroupDocumentID = makeGroupService.getUserGroupDocumentID(userId)

            withContext(Dispatchers.Main) {
                Log.d("MakeGroupViewModel", "✅ 가져온 userGroupDocumentID: $userGroupDocumentID")
                onResult(userGroupDocumentID)
            }
        }
    }
}