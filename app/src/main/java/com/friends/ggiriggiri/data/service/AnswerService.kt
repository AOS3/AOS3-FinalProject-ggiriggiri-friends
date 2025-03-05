package com.friends.ggiriggiri.data.service

import android.util.Log
import com.friends.ggiriggiri.data.model.AnswerModel
import com.friends.ggiriggiri.data.repository.AnswerRepository
import com.friends.ggiriggiri.data.vo.AnswerVO
import javax.inject.Inject

class AnswerService@Inject constructor(private val repository: AnswerRepository) {

    // 그룹별 질문문서에 하위 컬렉션으로 Answer를 넣는메서드
    suspend fun addAnswer(questionId: String, answer: AnswerModel) {
        repository.addAnswer(questionId,answer.toAnswerVO())
    }

    // 오늘 질문데이터를 가져오는 메서드
    suspend fun gettingQuestionDocumentIds(questionGroupDocumentID: String,groupDayFromCreate:String): List<String> {
        val list = repository.gettingQuestionDocumentIds(questionGroupDocumentID,groupDayFromCreate)
        list.forEach {
            Log.d("gettingQuestionDocumentIds",it)
        }
        return list
    }

    // 그룹아이디로 groupDayFromCreate를 가져오는메서드
    suspend fun gettingGroupDayFromCreate(groupDocumentID: String): String {
        val result = repository.gettingGroupDayFromCreate(groupDocumentID)
        return result
    }

    // 유저가 질문에 답했는지 안했는지
    fun checkUserAnswerExists(questionId: String, userId: String, onResult: (Boolean) -> Unit) {
        repository.checkUserAnswerExists(questionId, userId, onResult)
    }
}