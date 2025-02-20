package com.friends.ggiriggiri.data.service

import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.repository.RequestRepository
import com.friends.ggiriggiri.util.RequestState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestService @Inject constructor(
    private val requestRepository: RequestRepository
) {

    // Firestore에 요청 저장 후 문서 ID 반환
    suspend fun createRequest(
        userId: String,
        requestMessage: String,
        requestImage: String,
        groupDocumentId: String
    ): String? {
        val requestModel = RequestModel(
            requestUserDocumentID = userId,
            requestMessage = requestMessage,
            requestImage = requestImage,
            requestState = RequestState.ACTIVE.value,
            requestGroupDocumentID = groupDocumentId
        )
        return requestRepository.saveRequest(requestModel) // 저장 후 문서 ID 반환
    }
}
