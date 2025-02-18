package com.friends.ggiriggiri.data.service

import com.friends.ggiriggiri.data.model.QuestionListModel
import com.friends.ggiriggiri.data.repository.QuestionListRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionListService @Inject constructor (
    private val repository: QuestionListRepository
) {
    suspend fun getTodayQuestionList(): QuestionListModel? {
        val questionListVO = repository.getTodayQuestionList()
        return questionListVO?.let {
            QuestionListModel(
                color = it.questionColor,
                content = it.questionContent,
                imgUrl = it.questionImg,
                number = it.questionNumber
            )
        }
    }
}