package com.friends.ggiriggiri.ui.settinggroup.joingroup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.data.service.JoinGroupService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class JoinGroupViewModel @Inject constructor(
    private val joinGroupService: JoinGroupService
) : ViewModel() {

    fun joinGroup(userEmail: String, groupCode: String, groupPw: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val userDocumentID = joinGroupService.getUserDocumentIDByEmail(userEmail)
            if (userDocumentID == null) {
                withContext(Dispatchers.Main) { onResult(false) }
                return@launch
            }

            val groupDocumentID = joinGroupService.getGroupDocumentID(groupCode, groupPw)
            if (groupDocumentID == null) {
                withContext(Dispatchers.Main) { onResult(false) }
                return@launch
            }

            val isUserAdded = joinGroupService.addUserToGroup(groupDocumentID, userDocumentID)
            val isUserDataUpdated = joinGroupService.updateUserGroupDocumentID(userDocumentID, groupDocumentID)

            withContext(Dispatchers.Main) {
                if (isUserAdded && isUserDataUpdated) {
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
        }
    }

    fun getUserGroupDocumentID(userDocumentID: String, onResult: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val userGroupDocumentID = joinGroupService.getUserGroupDocumentID(userDocumentID)

            withContext(Dispatchers.Main) {
                Log.d("JoinGroupViewModel", "✅ 가져온 userGroupDocumentID: $userGroupDocumentID")
                onResult(userGroupDocumentID)
            }
        }
    }
}