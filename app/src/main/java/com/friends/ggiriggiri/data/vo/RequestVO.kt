package com.friends.ggiriggiri.data.vo

data class RequestVO(
    val requestTime: Long,
    val requestState: Int,
    val requestUserDocumentID: String,
    val requestMessage: String,
    val requestImage: String,
    val responseList: List<ResponseVO>,
    val requestGroupDocumentID: String
)