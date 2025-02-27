package com.friends.ggiriggiri.data.vo

import com.friends.ggiriggiri.data.model.AnswerModel
import com.google.firebase.firestore.DocumentId

data class AnswerVO (

    var answerMessage:String = "",
    var answerResponseTime:Long = 0L,
    var answerRequestState:Int = 1,
    var answerUserDocumentID:String = ""

){
    fun toAnswerModel(answerDocumentId: String):AnswerModel{
        val answerModel = AnswerModel()
        answerModel.answerDocumentID = answerDocumentId
        answerModel.answerMessage = answerMessage
        answerModel.answerResponseTime = answerResponseTime
        answerModel.answerRequestState = answerRequestState
        answerModel.answerUserDocumentID = answerUserDocumentID
        return answerModel
    }
}