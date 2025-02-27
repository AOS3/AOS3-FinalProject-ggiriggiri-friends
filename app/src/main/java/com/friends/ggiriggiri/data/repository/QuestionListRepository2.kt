package com.friends.ggiriggiri.data.repository

import android.util.Log
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.data.model.QuestionListItemModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuestionListRepository2 @Inject constructor(private val firestore: FirebaseFirestore, private val app: App){

    suspend fun fetchQuestionList(): List<QuestionListItemModel> { // 🔥 반환 타입 변경
        val questionList = mutableListOf<QuestionListItemModel>() // 🔥 리스트 타입 변경

        val userGroupDocumentID = app.loginUserModel.userGroupDocumentID
        Log.d("fetchQuestionList", "🔥 로그인한 유저의 그룹 ID: $userGroupDocumentID")

        val questionDataDocs = firestore.collection("QuestionData")
            .whereEqualTo("questionGroupDocumentID", userGroupDocumentID)
            .get().await()

        val questionDataMap = questionDataDocs.documents.associate { doc ->
            val questionListID = doc.get("questionListDocumentID")?.toString() ?: ""
            val questionCreateTime = doc.getLong("questionCreateTime") ?: 0L
            val questionDataDocumentID = doc.id  // 🔥 문서 ID 추가
            questionListID to Pair(questionCreateTime, questionDataDocumentID)
        }

        Log.d("fetchQuestionList", "🔥 매핑된 QuestionData: $questionDataMap")

        if (questionDataMap.isEmpty()) {
            Log.d("fetchQuestionList", "⏭️ 유저 그룹에 해당하는 질문이 없음")
            return emptyList()
        }

        val questionListIDs = questionDataMap.keys.toList()
        val questionCollection = firestore.collection("QuestionList")
            .whereIn("__name__", questionListIDs)
            .get().await()

        for (document in questionCollection.documents) {
            val documentID = document.id
            val data = document.data ?: continue
            val questionNumber = documentID.toIntOrNull()

            if (questionNumber == null) {
                Log.e("fetchQuestionList", "❌ 잘못된 문서 ID (숫자 변환 실패): $documentID")
                continue
            }

            val (questionCreateTime, questionDataDocumentID) = questionDataMap[documentID] ?: Pair(0L, "")
            Log.d("fetchQuestionList", "📅 가져온 questionCreateTime: $questionCreateTime")

            val question = QuestionListItemModel(
                content = data["questionContent"] as? String ?: "No Content",
                questionCreateTime = questionCreateTime,
                number = questionNumber,
                questionDataDocumentID = questionDataDocumentID  // 🔥 추가!
            )

            Log.d("fetchQuestionList", "✅ 추가된 질문: $question")
            questionList.add(question)
        }

        Log.d("fetchQuestionList", "🔥 최종 로드된 질문 개수: ${questionList.size}")
        return questionList.sortedByDescending { it.number }
    }
}