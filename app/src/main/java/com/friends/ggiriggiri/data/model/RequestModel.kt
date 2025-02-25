package com.friends.ggiriggiri.data.model

import com.friends.ggiriggiri.util.RequestState

data class RequestModel(
    var requestId: String = "",
    val requestTime: Long = System.currentTimeMillis(),
    val requestState: Int = RequestState.ACTIVE.value,
    val requestUserDocumentID: String = "",
    val requestMessage: String = "",
    val requestImage: String = "",
    val responseList: List<ResponseModel> = emptyList(),
    val requestGroupDocumentID: String = "",
    val requestUserProfileImage: String = "",
    val scheduledUpdate: Long? = System.currentTimeMillis() + (30 * 60 * 1000)
) {
    // Firestore 저장 변환 (객체 → Map)
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "requestTime" to requestTime,
            "requestState" to requestState,
            "requestUserDocumentID" to requestUserDocumentID,
            "requestMessage" to requestMessage,
            "requestImage" to requestImage,
            "responseList" to responseList.map { it.toMap() },
            "requestGroupDocumentID" to requestGroupDocumentID,
            "scheduledUpdate" to scheduledUpdate
        )
    }

    companion object {
        // Firestore에서 가져올 때 변환 (Map → 객체)
        fun fromFirestore(documentId: String, data: Map<String, Any>): RequestModel {
            return RequestModel(
                requestId = documentId, // 🔥 Firestore 문서 ID 추가
                requestTime = data["requestTime"] as? Long ?: System.currentTimeMillis(),
                requestState = (data["requestState"] as? Long)?.toInt() ?: RequestState.ACTIVE.value,
                requestUserDocumentID = data["requestUserDocumentID"] as? String ?: "",
                requestMessage = data["requestMessage"] as? String ?: "",
                requestImage = data["requestImage"] as? String ?: "",
                responseList = (data["responseList"] as? List<Map<String, Any>>)?.map { ResponseModel.fromMap(it) } ?: emptyList(),
                requestGroupDocumentID = data["requestGroupDocumentID"] as? String ?: "",
                scheduledUpdate = (data["scheduledUpdate"] as? Number)?.toLong() ?: (data["requestTime"] as? Long)?.plus(30 * 60 * 1000)
            )
        }
    }
}
