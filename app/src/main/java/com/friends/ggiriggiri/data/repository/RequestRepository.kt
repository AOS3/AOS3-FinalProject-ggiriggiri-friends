package com.friends.ggiriggiri.data.repository

import android.util.Log
import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.model.ResponseModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    // Firestore에 요청 저장 후 문서 ID 반환
    suspend fun saveRequest(requestModel: RequestModel, groupId: String, imageUrl: String): String? {
        return try {
            val documentRef = db.collection("Request")
                .add(requestModel.toMap()) // Map 형태로 저장
                .await()

            val requestId = documentRef.id

            // 그룹의 groupGallery 필드에 이미지 URL 추가
            db.collection("GroupData").document(groupId)
                .update("groupGallery", FieldValue.arrayUnion(imageUrl))
                .await()

            requestId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 요청한 사람의 정보를 가져오는 함수 추가
    fun getRequestUserInfo(documentId: String, onResult: (String?) -> Unit) {
        db.collection("UserData")
            .document(documentId) // 문서 ID로 직접 조회
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userName = document.getString("userName") ?: "알 수 없는 사용자"
                    onResult(userName) // 필요한 데이터(userName)만 반환
                } else {
                    onResult("알 수 없는 사용자") // 문서가 없을 경우 기본값 반환
                }
            }
            .addOnFailureListener {
                onResult("알 수 없는 사용자") // 오류 발생 시 기본값 반환
            }
    }

    suspend fun saveResponse(requestId: String, response: ResponseModel, groupId: String, imageUrl: String): Boolean {
        return try {
            db.collection("Request").document(requestId)
                .collection("Response")
                .add(response)
                .await()
            Log.d("RequestRepository", "응답 Firestore에 저장됨")

            // 그룹의 groupGallery 필드에 이미지 URL 추가
            Log.d("RequestRepository", "groupGallery 업데이트 시도: groupId=$groupId, imageUrl=$imageUrl")
            db.collection("GroupData").document(groupId)
                .update("groupGallery", FieldValue.arrayUnion(imageUrl))
                .await()

            Log.d("RequestRepository", "groupGallery에 이미지 추가 성공")
            true
        } catch (e: Exception) {
            Log.e("RequestRepository", "응답 저장 중 오류 발생: ${e.message}")
            false
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

    fun hasUserRequestedToday(userId: String, groupId: String, onResult: (Boolean) -> Unit) {
        val todayStart = getTodayStartTimestamp()
        val todayEnd = getTodayEndTimestamp()

        db.collection("Request")
            .whereEqualTo("requestUserDocumentID", userId) // 사용자가 요청한 내역인지 확인
            .whereEqualTo("requestGroupDocumentID", groupId) // 같은 그룹에서 요청한 내역인지 확인
            .whereGreaterThanOrEqualTo("requestTime", todayStart) // 오늘 날짜 이후인지 확인
            .whereLessThan("requestTime", todayEnd) // 오늘 날짜 안쪽인지 확인
            .get()
            .addOnSuccessListener { documents ->
                Log.d("RequestRepository", "오늘 요청한 문서 개수: ${documents.size()}") // 디버깅 로그
                onResult(!documents.isEmpty) // 문서가 있으면 true 반환 (이미 요청함)
            }
            .addOnFailureListener { exception ->
                Log.e("RequestRepository", "Firestore 요청 확인 실패: ${exception.message}")
                onResult(false) // 오류 발생 시 요청 가능하게 처리
            }
    }

    // 오늘 날짜 시작/끝 Timestamp 계산
    private fun getTodayStartTimestamp(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getTodayEndTimestamp(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }
}
