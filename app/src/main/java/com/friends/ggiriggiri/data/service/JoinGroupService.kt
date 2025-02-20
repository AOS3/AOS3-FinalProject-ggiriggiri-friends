package com.friends.ggiriggiri.data.service

import android.util.Log
import com.friends.ggiriggiri.data.repository.JoinGroupRepository
import javax.inject.Inject

class JoinGroupService @Inject constructor(
    private val joinGroupRepository: JoinGroupRepository
) {
    suspend fun getUserDocumentIDByEmail(email: String): String? {
        return joinGroupRepository.getUserDocumentIDByEmail(email)
    }

    suspend fun getGroupDocumentID(groupCode: String, groupPw: String): String? {
        return joinGroupRepository.getGroupDocumentID(groupCode, groupPw)
    }

    suspend fun addUserToGroup(groupDocumentID: String, userDocumentID: String): Boolean {
        return joinGroupRepository.addUserToGroup(groupDocumentID, userDocumentID)
    }

    suspend fun updateUserGroupDocumentID(userDocumentID: String, groupDocumentID: String): Boolean {
        Log.d("JoinGroupService", "🔥 유저 DocumentID: $userDocumentID / 그룹 DocumentID: $groupDocumentID")
        return joinGroupRepository.updateUserGroupDocumentID(userDocumentID, groupDocumentID)
    }
}