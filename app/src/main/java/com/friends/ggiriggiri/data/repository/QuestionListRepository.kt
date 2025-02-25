package com.friends.ggiriggiri.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.friends.ggiriggiri.data.vo.QuestionListVO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionListRepository @Inject constructor(
    private val db: FirebaseFirestore,
    application: Application
) {
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    suspend fun getTodayQuestionList(groupId: String): QuestionListVO? {
        return try {
            // 1. GroupData 컬렉션에서 groupDayFromCreate 값을 가져옴
            val groupSnapshot = db.collection("GroupData")
                .document(groupId)
                .get()
                .await()

            val groupDayFromCreate = groupSnapshot.getLong("groupDayFromCreate")?.toInt() ?: 1
            Log.d("QuestionListRepository", "groupDayFromCreate 값: $groupDayFromCreate")

            // 2. 질문 번호 결정 (5개 질문이므로 1~5 반복)
            val questionNumber = (groupDayFromCreate - 1) % 5 + 1
            Log.d("QuestionListRepository", "오늘의 질문 번호: $questionNumber")

            // 3. 해당 질문을 Firestore에서 가져옴
            val snapshot = db.collection("QuestionList")
                .document(questionNumber.toString())
                .get()
                .await()

            snapshot.toObject(QuestionListVO::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("QuestionListRepository", "Firestore 데이터 가져오기 실패: ${e.message}")
            null
        }
    }
}
