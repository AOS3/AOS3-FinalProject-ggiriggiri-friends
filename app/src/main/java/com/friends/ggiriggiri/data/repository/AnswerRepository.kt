package com.friends.ggiriggiri.data.repository

import com.friends.ggiriggiri.data.vo.AnswerVO
import com.friends.ggiriggiri.data.vo.QuestionVO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

class AnswerRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    // 그룹별 질문문서에 하위 컬렉션으로 Answer를 넣는메서드
    suspend fun addAnswer(questionId: String, answer: AnswerVO) {
        val answerCollection = firestore.collection("QuestionData")
            .document(questionId)
            .collection("Answer")

        answerCollection.document().set(answer)
    }

    // 오늘 질문데이터를 가져오는 메서드
    suspend fun gettingQuestionDocumentIds(questionGroupDocumentID: String,groupDayFromCreate:String): List<String> {
        return try {
            val snapshot = firestore.collection("QuestionData")
                .whereEqualTo("questionGroupDocumentID", questionGroupDocumentID)
                .whereEqualTo("questionListDocumentID",groupDayFromCreate.toInt())
                .get()
                .await()

            // 문서 ID만 리스트로 추출
            snapshot.documents.map { it.id }
        } catch (e: Exception) {
            emptyList() // 에러 발생 시 빈 리스트 반환
        }
    }

    // 그룹아이디로 groupDayFromCreate를 가져오는메서드
    suspend fun gettingGroupDayFromCreate(groupDocumentID: String): String {
        return try {
            val documentSnapshot = firestore.collection("GroupData")
                .document(groupDocumentID)
                .get()
                .await()

            if (!documentSnapshot.exists()) {
                return "문서 없음" // 문서 자체가 없음
            }

            var groupDayFromCreate = documentSnapshot.getDouble("groupDayFromCreate")?.toInt()!! // Int로 변환

            // 질문이생성되면서 자동으로 +1 되므로 오늘 질문에 답변을 하기위해 -1한다
            if (groupDayFromCreate != 1){
                groupDayFromCreate -= 1
            }


            groupDayFromCreate?.toString() ?: "필드 없음" // Int → String 변환 후 반환
        } catch (e: Exception) {
            "오류 발생: ${e.localizedMessage}" // 예외 발생 시 메시지 반환
        }
    }

    // 유저가 질문에 답했는지 안했는지
    fun checkUserAnswerExists(questionId: String, userId: String, onResult: (Boolean) -> Unit) {
        firestore.collection("QuestionData").document(questionId).collection("Answer")
            .whereEqualTo("answerUserDocumentID", userId) // 현재 사용자의 응답이 있는지 확인
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(!snapshot.isEmpty) // 응답이 있으면 true 반환
            }
            .addOnFailureListener {
                onResult(false) // 에러 발생 시 false 반환
            }
    }
}