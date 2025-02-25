package com.friends.ggiriggiri.data.repository

import android.util.Log
import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.model.ResponseModel
import com.friends.ggiriggiri.ui.first.register.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    // Firestore에 요청 저장 후 문서 ID 반환
    suspend fun saveRequest(requestModel: RequestModel): String? {
        return try {
            val documentRef = db.collection("Request")
                .add(requestModel.toMap()) // Map 형태로 저장
                .await()

            documentRef.id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 요청한 사람의 정보를 가져오는 함수 추가
    fun getRequestUserInfo(documentId: String, onResult: (String?) -> Unit) {
        db.collection("UserData")
            .document(documentId) // 🔥 문서 ID로 직접 조회
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userName = document.getString("userName") ?: "알 수 없는 사용자"
                    onResult(userName) // 🔥 필요한 데이터(userName)만 반환
                } else {
                    onResult("알 수 없는 사용자") // 문서가 없을 경우 기본값 반환
                }
            }
            .addOnFailureListener {
                onResult("알 수 없는 사용자") // 오류 발생 시 기본값 반환
            }
    }

    fun saveResponse(requestId: String, response: ResponseModel, onComplete: (Boolean) -> Unit) {
        db.collection("Request").document(requestId)
            .collection("Response")
            .add(response)
            .addOnSuccessListener {
                Log.d("Firestore", "응답 저장 성공")
                onComplete(true)
            }
            .addOnFailureListener {
                Log.e("Firestore", "응답 저장 실패: ${it.message}")
                onComplete(false)
            }
    }

    fun checkUserResponseExists(requestId: String, userId: String, onResult: (Boolean) -> Unit) {
        db.collection("Request").document(requestId).collection("Response")
            .whereEqualTo("responseUserDocumentID", userId) // 현재 사용자의 응답이 있는지 확인
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(!snapshot.isEmpty) // 응답이 있으면 true 반환
            }
            .addOnFailureListener {
                onResult(false) // 에러 발생 시 false 반환
            }
    }

    fun getLatestRequest(userGroupId: String, onResult: (RequestModel?) -> Unit) {
        db.collection("Request")
            .whereEqualTo("requestGroupDocumentID", userGroupId)
            .orderBy("requestTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .orderBy("__name__", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val document = snapshot.documents[0]
                    val latestRequest = document.toObject(RequestModel::class.java)?.apply {
                        requestId = document.id
                    }
                    Log.d("RequestRepository", "최신 요청 가져오기 성공: ${latestRequest?.requestId}")
                    onResult(latestRequest)
                } else {
                    Log.d("RequestRepository", "가져올 요청 없음")
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("RequestRepository", "요청 가져오기 실패: ${exception.message}", exception)
                onResult(null)
            }
    }

}
