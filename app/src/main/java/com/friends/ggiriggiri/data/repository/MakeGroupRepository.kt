package com.friends.ggiriggiri.data.repository

import android.util.Log
import com.friends.ggiriggiri.data.model.GroupModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MakeGroupRepository @Inject constructor(
    private val db: FirebaseFirestore
){

    // 그룹 코드 중복 체크
    suspend fun isGroupCodeExists(groupCode: String): Boolean {
        return try {
            val querySnapshot = db.collection("GroupData")
                .whereEqualTo("groupCode", groupCode)
                .limit(1)
                .get()
                .await()

            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.e("MakeGroupRepository", "그룹 코드 중복 체크 실패", e)
            false
        }
    }

    // 그룹 생성
    suspend fun createGroup(group: GroupModel): String? {
        return try {
            val groupRef = db.collection("GroupData").document() // 문서 ID 자동 생성
            val groupId = groupRef.id

            val updatedGroup = group.copy(groupRequestDocumentID = groupId)
            groupRef.set(updatedGroup, SetOptions.merge()).await()

            Log.d("MakeGroupRepository", "✅ 그룹 생성 완료! Firestore 문서 ID: $groupId")
            return groupId
        } catch (e: Exception) {
            Log.e("MakeGroupRepository", "❌ 그룹 생성 실패", e)
            null
        }
    }

    // UserData에 groupDocumentID 업데이트
    suspend fun updateUserGroup(userDocumentId: String, groupId: String) {
        try {
            val userRef = db.collection("UserData").document(userDocumentId)

            userRef.set(mapOf("userGroupDocumentID" to groupId), SetOptions.merge()).await()
            Log.d("MakeGroupRepository", "✅ Firestore에 userGroupDocumentID 업데이트 완료: $groupId")

            val updatedSnapshot = userRef.get().await()
            val updatedGroupID = updatedSnapshot.getString("userGroupDocumentID")
            Log.d("MakeGroupRepository", "🔍 Firestore에서 확인한 userGroupDocumentID: $updatedGroupID")

        } catch (e: Exception) {
            Log.e("MakeGroupRepository", "❌ UserData 그룹 ID 업데이트 실패", e)
        }
    }

    suspend fun getUserDocumentID(email: String): String? {
        return try {
            val querySnapshot = db.collection("UserData")
                .whereEqualTo("userId", email) // 이메일(userId)로 조회
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                querySnapshot.documents[0].id // 해당 유저의 documentID 반환
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("MakeGroupRepository", "UserData에서 documentID 가져오기 실패", e)
            null
        }
    }

    suspend fun getUserGroupDocumentID(userId: String): String? {
        return try {
            val querySnapshot = db.collection("UserData")
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                querySnapshot.documents[0].getString("userGroupDocumentID") ?: ""
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("MakeGroupRepository", "userGroupDocumentID 가져오기 실패: ${e.message}")
            null
        }
    }
}
