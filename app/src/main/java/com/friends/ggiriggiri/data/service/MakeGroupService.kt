package com.friends.ggiriggiri.data.service

import android.util.Log
import com.friends.ggiriggiri.data.model.GroupModel
import com.friends.ggiriggiri.data.repository.MakeGroupRepository
import javax.inject.Inject

class MakeGroupService @Inject constructor(
    val repository: MakeGroupRepository
) {

    // 그룹 생성 후 UserData 업데이트까지 처리
    suspend fun createGroupAndAssignToUser(
        userId: String,
        groupName: String,
        groupCode: String,
        groupPw: String
    ): String? {
        return try {
            if (repository.isGroupCodeExists(groupCode)) {
                Log.e("MakeGroupService", "❌ 그룹 코드 중복됨: $groupCode")
                return null
            }

            val newGroup = GroupModel(
                groupName = groupName,
                groupCode = groupCode,
                groupPw = groupPw,
                groupUserDocumentID = listOf(userId),
                groupCreateTime = System.currentTimeMillis(),
                groupDayFromCreate = 1 // 기본값 1
            )

            val groupDocumentId = repository.createGroup(newGroup)

            if (!groupDocumentId.isNullOrEmpty()) {
                Log.d("MakeGroupService", "✅ 그룹 생성 성공! 문서 ID: $groupDocumentId")

                repository.updateUserGroup(userId, groupDocumentId)

                val updatedDocumentID= repository.getUserGroupDocumentID(userId)
                Log.d("MakeGroupService", "🔥 최종 업데이트된 userGroupDocumentID: $updatedDocumentID")

                return updatedDocumentID
            }

            Log.e("MakeGroupService", "❌ 그룹 생성 실패 (문서 ID 없음)")
            null
        } catch (e: Exception) {
            Log.e("MakeGroupService", "❌ 그룹 생성 중 오류 발생", e)
            null
        }
    }

    suspend fun getUserDocumentID(email: String): String? {
        return repository.getUserDocumentID(email)
    }

    suspend fun getUserGroupDocumentID(userId: String): String? {
        return repository.getUserGroupDocumentID(userId)
    }
}
