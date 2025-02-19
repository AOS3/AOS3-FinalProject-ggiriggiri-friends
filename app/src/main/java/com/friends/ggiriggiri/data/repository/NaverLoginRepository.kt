package com.friends.ggiriggiri.data.repository

import android.util.Log
import com.friends.ggiriggiri.ui.first.register.UserModel
import com.friends.ggiriggiri.util.UserSocialLoginState
import com.friends.ggiriggiri.util.UserState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.UUID

class NaverLoginRepository {
    private val db = FirebaseFirestore.getInstance()

    // Firestore에서 이메일 기반으로 사용자 데이터 조회
    suspend fun getUserByEmail(email: String): UserModel? {
        return try {
            val querySnapshot = db.collection("UserData")
                .whereEqualTo("userId", email) // userId 기준 검색 (이메일)
                .limit(1)
                .get().await()

            if (!querySnapshot.isEmpty) {
                querySnapshot.documents[0].toObject(UserModel::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("NaverLoginRepository", "Firestore 사용자 조회 실패", e)
            null
        }
    }

    // Firestore에 새 사용자 저장
    suspend fun createUser(user: UserModel) {
        try {
            val userCollection = db.collection("UserData")

            // 기존 사용자가 있는지 확인 (이메일 기준)
            val querySnapshot = userCollection.whereEqualTo("userId", user.userId).limit(1).get().await()

            val userRef = if (!querySnapshot.isEmpty) {
                userCollection.document(querySnapshot.documents[0].id) // 기존 사용자 Document 사용
            } else {
                userCollection.document() // 새로운 사용자일 경우 자동 생성 Document ID 사용
            }

            // 기존 데이터 가져오기
            val documentSnapshot = userRef.get().await()
            val existingData = documentSnapshot.data?.toMutableMap() ?: mutableMapOf() // 기존 데이터 유지

            // 기존 데이터에 userJoinTime이 있으면 유지, 없으면 현재 시간 저장
            val joinTime = existingData["userJoinTime"] as? Long ?: System.currentTimeMillis()

            // 새로운 데이터 추가
            val userMap = mutableMapOf(
                "userId" to user.userId,
                "userName" to user.userName,
                "userProfileImage" to user.userProfileImage,
                "userJoinTime" to joinTime, // 기존 값 유지
                "userState" to user.userState.num, // 숫자로 저장
                "userSocialLogin" to user.userSocialLogin.num, // 숫자로 저장
                "userAutoLoginToken" to user.userAutoLoginToken,
                "userNaverToken" to user.userNaverToken,
                // 기존 필드 유지
                "userPw" to (existingData["userPw"] ?: ""),
                "userPhoneNumber" to (existingData["userPhoneNumber"] ?: ""),
                "userGroupDocumentID" to (existingData["userGroupDocumentID"] ?: ""),
                "userGoogleToken" to (existingData["userGoogleToken"] ?: ""),
                "userKakaoToken" to (existingData["userKakaoToken"] ?: ""),
                "userFcmCode" to (existingData["userFcmCode"] ?: mutableListOf<String>())
            )

            // Firestore에 기존 데이터 유지 + 새로운 데이터 추가 (`merge = true` 사용)
            userRef.set(userMap, SetOptions.merge()).await()
            Log.d("NaverLoginRepository", "Firestore 저장 성공 (Document ID: ${userRef.id})")

        } catch (e: Exception) {
            Log.e("NaverLoginRepository", "Firestore 사용자 저장 실패", e)
        }
    }

    // Firestore에서 사용자 조회 후 없으면 자동 회원가입
    suspend fun getOrCreateUser(email: String, userName: String, userProfileImage: String, naverToken: String): UserModel {
        val existingUser = getUserByEmail(email)
        return if (existingUser != null) {
            Log.d("NaverLoginRepository", "기존 사용자 Firestore에서 불러옴: ${existingUser.userId}")
            existingUser
        } else {
            val newUser = UserModel().apply {
                this.userId = email
                this.userName = userName
                this.userProfileImage = userProfileImage
                this.userJoinTime = System.currentTimeMillis()
                this.userState = UserState.NORMAL
                this.userSocialLogin = UserSocialLoginState.NAVER
                this.userAutoLoginToken = UUID.randomUUID().toString()
                this.userNaverToken = naverToken
            }
            createUser(newUser)
            newUser
        }
    }
}