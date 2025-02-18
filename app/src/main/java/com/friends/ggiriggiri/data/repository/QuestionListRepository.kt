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

    suspend fun getTodayQuestionList(): QuestionListVO? {
        return try {
            val isFirstRun = isAppRestarted() // 앱이 처음 실행됐는지 확인

            val questionNumber = if (isFirstRun) {
                val nextQuestionNumber = getNextQuestionNumber()
                Log.d("testQuestionListRepository", "앱 재시작 감지! 새로운 질문 번호: $nextQuestionNumber")

                // 새로운 질문 번호 저장
                sharedPreferences.edit()
                    .putInt("LAST_QUESTION_NUMBER", nextQuestionNumber)
                    .putBoolean("IS_APP_RESTARTED", false) // 재시작 이후에는 false로 변경
                    .commit() // 즉시 반영

                nextQuestionNumber
            } else {
                sharedPreferences.getInt("LAST_QUESTION_NUMBER", 1)
            }

            Log.d("testQuestionListRepository", "가져올 질문 번호: $questionNumber")

            val snapshot = db.collection("QuestionList")
                .document(questionNumber.toString())
                .get()
                .await()

            snapshot.toObject(QuestionListVO::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("testQuestionListRepository", "Firestore 데이터 가져오기 실패: ${e.message}")
            null
        }
    }

    private fun isAppRestarted(): Boolean {
        val isRestarted = sharedPreferences.getBoolean("IS_APP_RESTARTED", true)
        Log.d("testQuestionListRepository", "앱 재시작 여부 (읽기 후 재확인): $isRestarted")

        return isRestarted
    }

    private fun getNextQuestionNumber(): Int {
        val lastQuestionNumber = sharedPreferences.getInt("LAST_QUESTION_NUMBER", 1)
        return (lastQuestionNumber % 5) + 1 // 1~5까지 반복
    }
}
