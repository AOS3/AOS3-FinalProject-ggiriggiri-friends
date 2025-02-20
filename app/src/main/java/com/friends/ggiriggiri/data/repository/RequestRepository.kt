package com.friends.ggiriggiri.data.repository

import com.friends.ggiriggiri.data.model.RequestModel
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

    // Firestore에서 요청 가져오기 (문서 ID 포함)
    suspend fun getRequest(requestId: String): RequestModel? {
        return try {
            val snapshot = db.collection("Request").document(requestId).get().await()
            snapshot.data?.let { RequestModel.fromFirestore(snapshot.id, it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
