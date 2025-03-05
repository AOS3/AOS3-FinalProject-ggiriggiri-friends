package com.friends.ggiriggiri.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeRepository @Inject constructor(private val db: FirebaseFirestore) {

    fun getGroupName(groupId: String, onComplete: (String?) -> Unit) {
        db.collection("GroupData").document(groupId)
            .get()
            .addOnSuccessListener { document ->
                val groupName = document.getString("groupName")
                onComplete(groupName)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    fun getGroupUsers(groupId: String, onComplete: (List<String>) -> Unit) {
        db.collection("GroupData").document(groupId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userIds = document.get("groupUserDocumentID") as? List<String> ?: emptyList()
                    onComplete(userIds)
                } else {
                    onComplete(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                onComplete(emptyList())
            }
    }

    fun getUserProfiles(userIds: List<String>, onComplete: (List<Pair<String, String>>) -> Unit) {
        if (userIds.isEmpty()) {
            onComplete(emptyList())
            return
        }

        val userList = mutableListOf<Pair<String, String>>()
        var completedRequests = 0

        userIds.forEach { userId ->
            db.collection("UserData").document(userId) // 문서 ID 기반 조회
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("userName") ?: "이름 없음"
                        val image = document.getString("userProfileImage") ?: ""

                        Log.d("HomeRepository", "가져온 사용자 - userId: $userId, userName: $name, userProfileImage: $image")
                        userList.add(Pair(name, image))
                    } else {
                        Log.d("HomeRepository", "사용자 없음 - userId: $userId")
                    }

                    completedRequests++
                    if (completedRequests == userIds.size) {
                        Log.d("HomeRepository", "최종 변환된 사용자 리스트 크기: ${userList.size}")
                        onComplete(userList)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("HomeRepository", "Firestore 요청 실패 - userId: $userId, 오류: ${exception.message}")

                    completedRequests++
                    if (completedRequests == userIds.size) {
                        onComplete(userList)
                    }
                }
        }
    }

    suspend fun getRandomGalleryImages(groupId: String, count: Int = 5): List<String> {
        return try {
            val document = db.collection("GroupData").document(groupId).get().await()
            val galleryList = document.get("groupGallery") as? List<String> ?: emptyList()

            Log.d("GroupRepository", "전체 이미지 리스트: $galleryList")

            // 랜덤으로 5개 선택
            galleryList.shuffled().take(count)
        } catch (e: Exception) {
            Log.e("GroupRepository", "이미지 가져오기 실패: ${e.message}")
            emptyList()
        }
    }
}