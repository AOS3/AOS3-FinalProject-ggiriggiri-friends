package com.friends.ggiriggiri.data.model

import com.friends.ggiriggiri.data.vo.AnswerVO

data class AnswerModel (

    var answerDocumentID:String = "",
    var answerMessage:String = "",
    var answerResponseTime:Long = 0L,
    var answerRequestState:Int = 1,
    var answerUserDocumentID:String = ""

){
    fun toAnswerVO(): AnswerVO {
        val answerVO = AnswerVO()
        answerVO.answerMessage = answerMessage
        answerVO.answerResponseTime = answerResponseTime
        answerVO.answerRequestState = answerRequestState
        answerVO.answerUserDocumentID = answerUserDocumentID
        return answerVO
    }
}