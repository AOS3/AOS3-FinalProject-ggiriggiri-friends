package com.friends.ggiriggiri.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class JoinGroupRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // 그룹 코드 & 비밀번호로 해당 그룹의 documentID 찾기
    suspend fun getGroupDocumentID(groupCode: String, groupPw: String): String? {
        val querySnapshot = firestore.collection("GroupData")
            .whereEqualTo("groupCode", groupCode)
            .whereEqualTo("groupPw", groupPw) // ← "groupPw" 필드명 일치 여부 확인
            .limit(1)
            .get()
            .await()

        // 로그 추가: Firestore에서 조회한 데이터 확인
        if (querySnapshot.isEmpty) {
            Log.e("JoinGroupRepository", "그룹을 찾을 수 없음: groupCode=$groupCode, groupPw=$groupPw")
        } else {
            Log.d("JoinGroupRepository", "그룹 찾음! Document ID: ${querySnapshot.documents[0].id}")
        }

        return if (!querySnapshot.isEmpty) querySnapshot.documents[0].id else null
    }


    // UserData의 userGroupDocumentID 업데이트
    suspend fun updateUserGroupDocumentID(userDocumentID: String, groupDocumentID: String): Boolean {
        return try {
            val userRef = firestore.collection("UserData").document(userDocumentID)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val existingGroupID = snapshot.getString("userGroupDocumentID")

                if (existingGroupID.isNullOrEmpty()) {
                    transaction.update(userRef, "userGroupDocumentID", groupDocumentID)
                } else {
                    val updatedList = listOf(existingGroupID, groupDocumentID).distinct()
                    transaction.update(userRef, "userGroupDocumentID", updatedList)
                }
            }.await()

            Log.d("JoinGroupRepository", "UserData 업데이트 완료: $userDocumentID → $groupDocumentID")
            true
        } catch (e: Exception) {
            Log.e("JoinGroupRepository", "UserData 업데이트 실패: ${e.message}")
            false
        }
    }

    // 그룹에 사용자 documentID 추가
    suspend fun addUserToGroup(groupDocumentID: String, userDocumentID: String): Boolean {
        return try {
            val groupRef = firestore.collection("GroupData").document(groupDocumentID)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(groupRef)
                val currentUsers = snapshot.get("groupUserDocumentID") as? MutableList<String> ?: mutableListOf()

                if (!currentUsers.contains(userDocumentID)) {
                    currentUsers.add(userDocumentID)
                    transaction.update(groupRef, "groupUserDocumentID", currentUsers)
                }
            }.await()

            Log.d("JoinGroupRepository", "유저 추가 완료: $userDocumentID → $groupDocumentID")
            true
        } catch (e: Exception) {
            Log.e("JoinGroupRepository", "유저 추가 실패: ${e.message}")
            false
        }
    }

    suspend fun getUserDocumentIDByEmail(email: String): String? {
        return try {
            val querySnapshot = firestore.collection("UserData")
                .whereEqualTo("userId", email) // 이메일로 검색
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                querySnapshot.documents[0].id // documentID 반환
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("JoinGroupRepository", "유저 DocumentID 조회 실패: ${e.message}")
            null
        }
    }

    suspend fun getUserGroupDocumentID(userDocumentID: String): String? {
        return try {
            val userRef = firestore.collection("UserData").document(userDocumentID).get().await()
            userRef.getString("userGroupDocumentID")
        } catch (e: Exception) {
            Log.e("JoinGroupRepository", "userGroupDocumentID 가져오기 실패: ${e.message}")
            null
        }
    }


}