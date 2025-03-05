package com.friends.ggiriggiri.data.service

import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.repository.RequestDetailRepository
import javax.inject.Inject

class RequestDetailService @Inject constructor(private val repository: RequestDetailRepository) {

    suspend fun getRequestDetail(requestId: String): RequestModel? {
        return repository.fetchRequestDetail(requestId)
    }

    suspend fun getUserProfileImage(userDocumentID: String): String {
        return repository.fetchUserProfileImage(userDocumentID)
    }
}