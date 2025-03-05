package com.friends.ggiriggiri.data.service

import com.friends.ggiriggiri.data.model.QuestionListItemModel
import com.friends.ggiriggiri.data.repository.QuestionListRepository2
import javax.inject.Inject

class QuestionListService2 @Inject constructor(private val repository: QuestionListRepository2){

    suspend fun getQuestionList(): List<QuestionListItemModel> {
        return repository.fetchQuestionList()
    }
}