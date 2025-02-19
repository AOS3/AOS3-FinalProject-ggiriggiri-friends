package com.friends.ggiriggiri.ui.first.register

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



    }

}