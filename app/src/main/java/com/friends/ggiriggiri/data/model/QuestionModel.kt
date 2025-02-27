package com.friends.ggiriggiri.data.model

import com.friends.ggiriggiri.data.vo.QuestionVO

data class QuestionModel (
    var questionDocumentID:String = "",
    var questionCreateTime:Long = 0L,
    var questionGroupDocumentID:String = "",
    var questionListDocumentID:Int = 1,
    var questionState:Int = 1
){
    fun toQuestionVO(): QuestionVO {
        val questionVO = QuestionVO()
        questionVO.questionCreateTime = questionCreateTime
        questionVO.questionGroupDocumentID = questionGroupDocumentID
        questionVO.questionListDocumentID = questionListDocumentID
        questionVO.questionState = questionState
        return questionVO
    }
}