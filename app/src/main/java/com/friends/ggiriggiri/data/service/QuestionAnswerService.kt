package com.friends.ggiriggiri.data.service

import com.friends.ggiriggiri.data.model.QuestionAnswerModel
import com.friends.ggiriggiri.data.model.QuestionListModel
import com.friends.ggiriggiri.data.repository.QuestionAnswerRepository
import javax.inject.Inject

class QuestionAnswerService @Inject constructor(private val repository: QuestionAnswerRepository) {

    suspend fun getQuestionData(questionListDocumentID: String): QuestionListModel {
        return repository.fetchQuestionData(questionListDocumentID)
    }

    suspend fun getAnswers(questionDataDocumentID: String): List<QuestionAnswerModel> {
        return repository.fetchAnswers(questionDataDocumentID)
    }
}