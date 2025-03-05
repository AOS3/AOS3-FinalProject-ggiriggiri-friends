package com.friends.ggiriggiri.data.service

import com.friends.ggiriggiri.data.model.QuestionListModel
import com.friends.ggiriggiri.data.repository.QuestionListRepository
import com.friends.ggiriggiri.data.vo.QuestionListVO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionListService @Inject constructor (
    private val repository: QuestionListRepository
) {
    suspend fun getTodayQuestion(groupId: String): QuestionListVO? {
        return repository.getTodayQuestionList(groupId)
    }
}