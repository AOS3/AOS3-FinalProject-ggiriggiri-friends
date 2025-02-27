package com.friends.ggiriggiri.data.vo

import com.friends.ggiriggiri.data.model.QuestionModel

data class QuestionVO (
    var questionCreateTime:Long = 0L,
    var questionGroupDocumentID:String = "",
    var questionListDocumentID:String = "",
    var questionState:Int = 1
){
    fun toQuestionModel(questionDocumentID:String): QuestionModel{
        val questionModel = QuestionModel()
        questionModel.questionDocumentID = questionDocumentID
        questionModel.questionCreateTime = questionCreateTime
        questionModel.questionGroupDocumentID = questionListDocumentID
        questionModel.questionListDocumentID = questionListDocumentID
        questionModel.questionState = questionState
        return questionModel
    }
}