package com.friends.ggiriggiri.data.repository

import android.util.Log
import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.model.ResponseModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RequestListRepository @Inject constructor(private val firestore: FirebaseFirestore){

    suspend fun fetchRequests(userGroupDocumentID: String): List<RequestModel> {
        val requestCollection = firestore.collection("Request")

        // 🔥 Firestore에서 requestGroupDocumentID가 userGroupDocumentID와 같은 데이터만 가져옴
        val requestDocs = requestCollection
            .whereEqualTo("requestGroupDocumentID", userGroupDocumentID)
            .get()
            .await()

        // 🔥 사용자 ID 리스트 수집 (중복 제거)
        val userIds = requestDocs.documents.mapNotNull { it["requestUserDocumentID"] as? String }.distinct()
        val userNames = fetchUserNames(userIds) // 🔥 여러 사용자 이름을 한 번에 가져오기

        return requestDocs.documents.mapNotNull { document ->
            val data = document.data ?: return@mapNotNull null
            val requestId = document.id

            RequestModel(
                requestId = requestId,
                requestTime = data["requestTime"] as? Long ?: System.currentTimeMillis(),
                requestState = 0, // 필요 없는 데이터라 기본값 0 설정
                requestUserDocumentID = userNames[data["requestUserDocumentID"] as? String] ?: "Unknown",
                requestMessage = data["requestMessage"] as? String ?: "",
                requestImage = "", // 필요 없는 데이터라 빈 값 설정
                responseList = emptyList(), // 필요 없는 데이터라 빈 리스트 설정
                requestGroupDocumentID = userGroupDocumentID
            )
        }.sortedByDescending { it.requestTime }
    }

    private suspend fun fetchUserNames(userIds: List<String>): Map<String, String> {
        if (userIds.isEmpty()) return emptyMap()

        Log.d("fetchUserNames", "🔥 Fetching names for userIds: $userIds")

        val userCollection = firestore.collection("UserData")
        val userDocs = userCollection.get().await() // 🔥 전체 문서를 한 번에 가져옴

        val userNames = mutableMapOf<String, String>()

        for (userDoc in userDocs.documents) {
            val userId = userDoc.id
            if (userId in userIds) {
                val userName = userDoc.getString("userName") ?: "알 수 없음"
                userNames[userId] = userName
                Log.d("fetchUserNames", "🔥 Retrieved userName: $userName for userID: $userId")
            }
        }

        return userNames
    }

    private suspend fun fetchResponses(requestId: String): List<ResponseModel> {
        val responseList = mutableListOf<ResponseModel>()
        val responseCollection = firestore.collection("Request").document(requestId).collection("Response")

        val responseDocs = responseCollection.get().await()
        for (document in responseDocs.documents) {
            val data = document.data ?: continue
            val userDocumentID = data["responseUserDocumentID"] as? String ?: ""

            Log.d("fetchResponses", "🔥 Fetching userName for userDocumentID: $userDocumentID") // ✅ 로그 추가

            val userName = fetchUserName(userDocumentID)

            Log.d("fetchResponses", "🔥 Retrieved userName: $userName")

            val response = ResponseModel(
                responseTime = data["responseTime"] as? Long ?: System.currentTimeMillis(),
                // requestState = (data["requestState"] as? Long)?.toInt() ?: 0,
                responseImage = data["responseImage"] as? String ?: "",
                responseMessage = data["responseMessage"] as? String ?: "",
                responseUserDocumentID = userName
            )
            responseList.add(response)
        }

        return responseList
    }

    private suspend fun fetchUserName(userDocumentID: String): String {
        Log.d("fetchUserName", "🔥 userDocumentID: $userDocumentID")

        val userDoc = firestore.collection("UserData").document(userDocumentID).get().await()

        val userName = userDoc.getString("userName") ?: "알 수 없음"

        Log.d("fetchUserName", "🔥 Retrieved userName: $userName")

        return userName
    }

    private suspend fun fetchUserProfileImage(userDocumentID: String): String {
        val userDoc = firestore.collection("UserData").document(userDocumentID).get().await()
        val profileImage = userDoc.getString("userProfileImage") ?: ""

        return profileImage
    }
}