package com.friends.ggiriggiri.data.service

import android.util.Log
import com.friends.ggiriggiri.data.repository.KakaoLoginRepository
import com.friends.ggiriggiri.ui.first.register.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KakaoLoginService(private val repository: KakaoLoginRepository) {

    // 카카오 로그인 후 Firestore에서 사용자 정보 가져오기
    suspend fun handleKakaoLogin(email: String, userName: String, userProfileImage: String, kakaoToken: String): UserModel? {
        return try {
            withContext(Dispatchers.IO) {
                val user = repository.getOrCreateUser(email, userName, userProfileImage, kakaoToken)
                Log.d("KakaoLoginService", "Firestore에서 유저 처리 완료: ${user.userId}")
                user
            }
        } catch (e: Exception) {
            Log.e("KakaoLoginService", "Firestore 사용자 처리 중 오류 발생", e)
            null
        }
    }
}