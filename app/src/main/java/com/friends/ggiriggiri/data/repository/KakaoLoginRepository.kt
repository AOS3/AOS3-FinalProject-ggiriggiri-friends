package com.friends.ggiriggiri.data.repository

import android.util.Log
import com.friends.ggiriggiri.ui.start.register.UserModel
import com.friends.ggiriggiri.ui.start.register.UserVO
import com.friends.ggiriggiri.util.UserSocialLoginState
import com.friends.ggiriggiri.util.UserState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import java.util.UUID

class KakaoLoginRepository {
    private val db = FirebaseFirestore.getInstance()

    // Firestore에서 이메일 기반으로 사용자 데이터 조회
    suspend fun getUserByEmail(email: String): UserVO? {
        return try {
            val querySnapshot = db.collection("UserData")
                .whereEqualTo("userId", email) // userId 기준 검색 (이메일)
                .limit(1)
                .get().await()
            if (!querySnapshot.isEmpty) {
                querySnapshot.documents[0].toObject(UserVO::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("KaKaoLoginRepository", "Firestore 사용자 조회 실패", e)
            null
        }
    }

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

            // UserModel을 UserVO로 변환
            val userVO = user.toUserVO()
            userVO.userJoinTime = joinTime // 기존 값 유지

            // Firestore에 기존 데이터 유지 + 새로운 데이터 추가 (`merge = true` 사용)
            userRef.set(userVO, SetOptions.merge()).await()
            Log.d("KakaoLoginRepository", "Firestore 저장 성공 (Document ID: ${userRef.id})")

        } catch (e: Exception) {
            Log.e("KakaoLoginRepository", "Firestore 사용자 저장 실패", e)
        }
    }




    // Firestore에서 사용자 조회 후 없으면 자동 회원가입
    suspend fun getOrCreateUser(email: String, userName: String, userProfileImage: String, kakaoToken: String): UserModel {
        val existingUserVO = getUserByEmail(email)
        return if (existingUserVO != null) {
            Log.d("KaKaoLoginRepository", "기존 사용자 Firestore에서 불러옴: ${existingUserVO.userId}")
            val documentId = db.collection("UserData").whereEqualTo("userId", email).limit(1).get().await().documents.firstOrNull()?.id ?: ""

            // 기존 사용자가 있으면 FCM 토큰을 업데이트
            updateFcmToken(documentId)

            existingUserVO.toUserModel(documentId)
        } else {
            val newUser = UserModel().apply {
                this.userId = email
                this.userName = userName
                this.userProfileImage = userProfileImage
                this.userJoinTime = System.currentTimeMillis()
                this.userState = UserState.NORMAL
                this.userSocialLogin = UserSocialLoginState.KAKAO
                this.userAutoLoginToken = UUID.randomUUID().toString()
                this.userKakaoToken = kakaoToken
            }
            createUser(newUser)

            // Firestore에서 생성된 Document ID를 가져와 모델에 설정
            val createdUserDoc = db.collection("UserData").whereEqualTo("userId", email).limit(1).get().await().documents.firstOrNull()
            val documentId = createdUserDoc?.id ?: ""

            // 새 유저일 경우에도 FCM 토큰 추가
            updateFcmToken(documentId)

            createdUserDoc?.toObject(UserVO::class.java)?.toUserModel(documentId) ?: newUser
        }
    }

    suspend fun updateFcmToken(userDocumentId: String) {
        try {
            val userRef = db.collection("UserData").document(userDocumentId)

            // 현재 기기의 FCM 토큰 가져오기
            val newFcmToken = FirebaseMessaging.getInstance().token.await()

            if (newFcmToken.isNotEmpty()) {
                // 기존 FCM 토큰 리스트 가져오기
                val documentSnapshot = userRef.get().await()
                val existingData = documentSnapshot.toObject(UserVO::class.java)

                val existingFcmTokens = existingData?.userFcmCode ?: mutableListOf()

                // 새로운 토큰이 기존 리스트에 없으면 추가
                if (!existingFcmTokens.contains(newFcmToken)) {
                    existingFcmTokens.add(newFcmToken)
                    userRef.update("userFcmCode", existingFcmTokens).await()
                    Log.d("GoogleLoginRepository", "FCM 토큰 업데이트 성공: $newFcmToken")
                } else {
                    Log.d("GoogleLoginRepository", "이미 등록된 FCM 토큰: $newFcmToken")
                }
            }
        } catch (e: Exception) {
            Log.e("GoogleLoginRepository", "FCM 토큰 업데이트 실패", e)
        }
    }
}

