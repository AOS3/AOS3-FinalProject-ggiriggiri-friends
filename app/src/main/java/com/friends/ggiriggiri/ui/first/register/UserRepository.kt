package com.friends.ggiriggiri.ui.first.register

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class UserRepository() {
    companion object{
        // 회원가입(유저추가)
        suspend fun addUser(userVO: UserVO){
            val firestore = FirebaseFirestore.getInstance()
            val collectionReference = firestore.collection("UserData")
            collectionReference.add(userVO)
        }

        // 사용자 아이디와 동일한 사용자의 정보 하나를 반환하는 메서드(없으면 null 반환)
        suspend fun login(userId: String, userPw: String): MutableMap<String, *>? {
            val firestore = FirebaseFirestore.getInstance()
            val collectionReference = firestore.collection("UserData")

            val result = collectionReference
                .whereEqualTo("userId", userId)
                .whereEqualTo("userPw", userPw)
                .get()
                .await()

            if (result.isEmpty) {
                return null
            }

            val userVoList = result.toObjects(UserVO::class.java)
            return mutableMapOf(
                "user_document_id" to result.documents[0].id,
                "user_vo" to userVoList[0]
            )
        }

        // 아이디 중복체크(true: 사용가능,false: 사용불가능)
        suspend fun duplicationCheckUserId(userId:String):Boolean{
            val firestore = FirebaseFirestore.getInstance()
            val collectionReference = firestore.collection("UserData")

            val result = collectionReference
                .whereEqualTo("userId",userId)
                .get()
                .await()

            if (result.isEmpty) {
                // 사용가능
                return true
            }else{
                // 사용 불가능
                return false
            }

        }


        // 아이디 찾기
        suspend fun findId(userName: String, userPhoneNumber: String): String {
            val firestore = FirebaseFirestore.getInstance()
            val collectionReference = firestore.collection("UserData")

            val result = collectionReference
                .whereEqualTo("userName", userName)
                .whereEqualTo("userPhoneNumber", userPhoneNumber)
                .get()
                .await()

            return if (result.isEmpty) {
                // 해당 이름과 번호로 아이디 없음
                ""
            } else {
                // 첫 번째 결과의 userId 필드 반환
                result.documents[0].getString("userId") ?: ""
            }
        }

        // 아이디와 전화번호로 해당유저가 있는지 검색하기(FindPwFragment)
        suspend fun findUserByIdAndPhoneNumber(userId: String, userPhoneNumber: String): Map<String, UserVO?>? {
            val firestore = FirebaseFirestore.getInstance()
            val collectionReference = firestore.collection("UserData")

            val result = collectionReference
                .whereEqualTo("userId", userId)
                .whereEqualTo("userPhoneNumber", userPhoneNumber)
                .get()
                .await()

            if (result.isEmpty) {
                // 해당 아이디와 번호로 사용자를 찾을 수 없음
                return null
            } else {
                val document = result.documents.first()
                val user = document.toObject(UserVO::class.java)
                val documentId = document.id

                return mapOf(documentId to user)
            }
        }

        //비밀번호 재설정
        fun resetUserPw(userDocumentId: String, pw: String) {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("UserData").document(userDocumentId)
            userRef.update("userPw", pw)
                .addOnSuccessListener {
                    Log.d("ResetPw", "비밀번호 변경 성공")
                }
                .addOnFailureListener { e ->
                    Log.e("ResetPw", "비밀번호 변경 실패: ${e.message}")
                }
        }



    }

}