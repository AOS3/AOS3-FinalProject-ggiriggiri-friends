package com.friends.ggiriggiri.data.service

import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.repository.RequestListRepository
import javax.inject.Inject

class RequestListService @Inject constructor(private val repository: RequestListRepository) {

    suspend fun getRequestList(userGroupDocumentID: String): List<RequestModel> {
        return repository.fetchRequests(userGroupDocumentID)
    }
}