package com.friends.ggiriggiri.data.repository

import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.model.ResponseModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RequestDetailRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun fetchRequestDetail(requestId: String): RequestModel? {
        val document = firestore.collection("Request").document(requestId).get().await()
        val data = document.data ?: return null

        val responseList = fetchResponses(requestId)
        val userDocumentID = data["requestUserDocumentID"] as? String ?: ""
        val userName = fetchUserName(userDocumentID)
        val userProfileImage = fetchUserProfileImage(userDocumentID)

        return RequestModel(
            requestId = requestId,
            requestTime = data["requestTime"] as? Long ?: System.currentTimeMillis(),
            requestState = (data["requestState"] as? Long)?.toInt() ?: 0,
            requestUserDocumentID = userName,
            requestMessage = data["requestMessage"] as? String ?: "",
            requestImage = data["requestImage"] as? String ?: "",
            responseList = responseList,
            requestGroupDocumentID = data["requestGroupDocumentID"] as? String ?: "",
            requestUserProfileImage = userProfileImage
        )
    }

    private suspend fun fetchResponses(requestId: String): List<ResponseModel> {
        val responseList = mutableListOf<ResponseModel>()
        val responseCollection = firestore.collection("Request").document(requestId).collection("Response")

        val responseDocs = responseCollection.get().await()
        for (document in responseDocs.documents) {
            val data = document.data ?: continue

            val userDocumentID = data["responseUserDocumentID"] as? String ?: ""
            val userName = fetchUserName(userDocumentID)
            val profileImage = fetchUserProfileImage(userDocumentID)

            val response = ResponseModel(
                responseTime = data["responseTime"] as? Long ?: System.currentTimeMillis(),
                responseImage = data["responseImage"] as? String ?: "",
                responseMessage = data["responseMessage"] as? String ?: "",
                responseUserDocumentID = userName,
                responseUserProfileImage = profileImage
            )

            responseList.add(response)
        }

        return responseList
    }

    private suspend fun fetchUserName(userDocumentID: String): String {
        val userDoc = firestore.collection("UserData").document(userDocumentID).get().await()
        return userDoc.getString("userName") ?: "알 수 없음"
    }

    suspend fun fetchUserProfileImage(userDocumentID: String): String {
        val userDoc = firestore.collection("UserData").document(userDocumentID).get().await()
        val profileImage = userDoc.getString("userProfileImage") ?: ""

        return profileImage
    }
}