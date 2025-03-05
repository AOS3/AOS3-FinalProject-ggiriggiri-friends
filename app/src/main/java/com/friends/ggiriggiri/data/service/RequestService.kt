package com.friends.ggiriggiri.data.service

import android.util.Log
import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.model.ResponseModel
import com.friends.ggiriggiri.data.repository.RequestRepository
import com.friends.ggiriggiri.ui.first.register.UserModel
import com.friends.ggiriggiri.util.RequestState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestService @Inject constructor(
    private val requestRepository: RequestRepository
) {

    // Firestore에 요청 저장 후 문서 ID 반환
    suspend fun saveRequest(
        userId: String,
        requestMessage: String,
        requestImage: String,
        groupDocumentId: String
    ): String? {
        val requestModel = RequestModel(
            requestUserDocumentID = userId,
            requestMessage = requestMessage,
            requestImage = requestImage,
            requestGroupDocumentID = groupDocumentId
        )
        return requestRepository.saveRequest(requestModel, groupDocumentId, requestImage)
    }

    // Firestore에 응답 저장
    suspend fun saveResponse(
        requestId: String,
        responseModel: ResponseModel,
        groupDocumentId: String,
        responseImage: String
    ): Boolean {
        return requestRepository.saveResponse(requestId, responseModel, groupDocumentId, responseImage)
    }

    // 요청한 사용자의 정보 가져오기
    fun fetchRequestUserInfo(documentId: String, onResult: (String?) -> Unit) {
        requestRepository.getRequestUserInfo(documentId, onResult)
    }

    fun checkUserResponseExists(requestId: String, userId: String, onResult: (Boolean) -> Unit) {
        requestRepository.checkUserResponseExists(requestId, userId, onResult)
    }

    fun fetchLatestRequest(userGroupId: String, onResult: (RequestModel?) -> Unit) {
        requestRepository.getLatestRequest(userGroupId) { request ->
            if (request != null) {
                Log.d("RequestService", "최신 요청 가져오기 완료: ${request.requestId}")
            } else {
                Log.d("RequestService", "가져올 요청 없음")
            }
            onResult(request)
        }
    }

    fun hasUserRequestedToday(userId: String, groupId: String, onResult: (Boolean) -> Unit) {
        requestRepository.hasUserRequestedToday(userId, groupId, onResult)
    }
}
