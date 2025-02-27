package com.friends.ggiriggiri.data.repository

import android.util.Log
import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.model.ResponseModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RequestListRepository @Inject constructor(private val firestore: FirebaseFirestore){

    suspend fun fetchRequests(userGroupDocumentID: String): List<RequestModel> {
        val requestList = mutableListOf<RequestModel>()
        val requestCollection = firestore.collection("Request")

        val requestDocs = requestCollection.get().await()

        for (document in requestDocs.documents) {
            val data = document.data ?: continue
            val requestId = document.id

            val requestGroupDocumentID = data["requestGroupDocumentID"] as? String ?: ""

            // 🔥 로그인한 유저의 그룹 ID와 요청의 그룹 ID가 같은 경우만 추가
            if (requestGroupDocumentID == userGroupDocumentID) {
                val responseList = fetchResponses(requestId)
                val userName = fetchUserName(data["requestUserDocumentID"] as String)

                val request = RequestModel(
                    requestId = requestId,
                    requestTime = data["requestTime"] as? Long ?: System.currentTimeMillis(),
                    requestState = (data["requestState"] as? Long)?.toInt() ?: 0,
                    requestUserDocumentID = userName,
                    requestMessage = data["requestMessage"] as? String ?: "",
                    requestImage = data["requestImage"] as? String ?: "",
                    responseList = responseList,
                    requestGroupDocumentID = requestGroupDocumentID
                )

                requestList.add(request)
            }
        }

        return requestList.sortedByDescending { it.requestTime }
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