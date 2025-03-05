package com.friends.ggiriggiri.data.vo

import com.friends.ggiriggiri.data.model.QuestionModel

data class QuestionVO (
    var questionCreateTime:Long = 0L,
    var questionGroupDocumentID:String = "",
    var questionListDocumentID:Int = 0,
    var questionState:Int = 1
){
    fun toQuestionModel(questionDocumentID:String): QuestionModel{
        val questionModel = QuestionModel()
        questionModel.questionDocumentID = questionDocumentID
        questionModel.questionCreateTime = questionCreateTime
        questionModel.questionGroupDocumentID = questionGroupDocumentID
        questionModel.questionListDocumentID = questionListDocumentID
        questionModel.questionState = questionState
        return questionModel
    }
}