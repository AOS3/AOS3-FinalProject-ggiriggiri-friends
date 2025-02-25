package com.friends.ggiriggiri.data.model

import com.friends.ggiriggiri.util.RequestState

data class ResponseModel(
    val responseTime: Long = System.currentTimeMillis(), // 응답 생성 시간
    val responseState: Int = RequestState.ACTIVE.value, // 응답 상태
    val responseImage: String = "", // 응답 이미지 URL
    val responseMessage: String = "", // 응답 메시지
    val responseUserDocumentID: String = "", // 응답한 사용자 ID
    val responseUserProfileImage: String = "" // 응답한 사용자 프로필 이미지 URL
){
    // Firestore 저장 시 변환 (객체 → Map)
    fun toMap(): Map<String, Any> {
        return mapOf(
            "responseTime" to responseTime,
            "requestState" to responseState,
            "responseImage" to responseImage,
            "responseMessage" to responseMessage,
            "responseUserDocumentID" to responseUserDocumentID
        )
    }

    companion object {
        // Firestore에서 가져올 때 변환 (Map → 객체)
        fun fromMap(data: Map<String, Any>): ResponseModel {
            return ResponseModel(
                responseTime = data["responseTime"] as? Long ?: System.currentTimeMillis(),
                responseState = (data["requestState"] as? Long)?.toInt() ?: RequestState.ACTIVE.value,
                responseImage = data["responseImage"] as? String ?: "",
                responseMessage = data["responseMessage"] as? String ?: "",
                responseUserDocumentID = data["responseUserDocumentID"] as? String ?: ""
            )
        }
    }
}