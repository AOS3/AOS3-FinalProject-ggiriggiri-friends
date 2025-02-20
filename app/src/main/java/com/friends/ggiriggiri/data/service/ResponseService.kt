package com.friends.ggiriggiri.data.service

import com.friends.ggiriggiri.data.repository.ResponseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResponseService @Inject constructor(
    private val responseRepository: ResponseRepository
) {

}