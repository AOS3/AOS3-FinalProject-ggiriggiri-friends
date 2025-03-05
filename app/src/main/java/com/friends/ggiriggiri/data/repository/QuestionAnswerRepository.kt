package com.friends.ggiriggiri.data.repository

import com.friends.ggiriggiri.data.model.QuestionAnswerModel
import com.friends.ggiriggiri.data.model.QuestionListModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuestionAnswerRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun fetchQuestionData(questionListDocumentID: String): QuestionListModel {
        val document = firestore.collection("QuestionList").document(questionListDocumentID).get().await()

        return QuestionListModel(
            color = document.getString("questionColor") ?: "#FFFFFF",
            content = document.getString("questionContent") ?: "No Content",
            imgUrl = document.getString("questionImg") ?: "",
            number = questionListDocumentID.toIntOrNull() ?: 0
        )
    }

    suspend fun fetchAnswers(questionDataDocumentID: String): List<QuestionAnswerModel> {
        val answers = mutableListOf<QuestionAnswerModel>()

        val answerDocs = firestore.collection("QuestionData")
            .document(questionDataDocumentID)
            .collection("Answer")
            .get()
            .await()

        for (doc in answerDocs.documents) {
            val answerMessage = doc.getString("answerMessage") ?: ""
            val answerResponseTime = doc.getLong("answerResponseTime") ?: 0L
            val answerUserDocumentID = doc.getString("answerUserDocumentID") ?: ""

            // UserData에서 유저 이름과 프로필 이미지 가져오기
            val userDoc = firestore.collection("UserData").document(answerUserDocumentID).get().await()
            val userName = userDoc.getString("userName") ?: "알 수 없음"
            val userProfileImage = userDoc.getString("userProfileImage") ?: ""



            val answer = QuestionAnswerModel(
                answerMessage = answerMessage,
                answerResponseTime = answerResponseTime,
                answerUserDocumentID = answerUserDocumentID,
                userName = userName,
                userProfileImage = userProfileImage
            )
            answers.add(answer)
        }

        return answers.sortedBy { it.answerResponseTime }
    }
}