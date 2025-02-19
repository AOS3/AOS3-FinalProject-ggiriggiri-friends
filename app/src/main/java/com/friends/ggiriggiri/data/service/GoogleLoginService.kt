package com.friends.ggiriggiri.data.service

import android.util.Log
import com.friends.ggiriggiri.data.repository.GoogleLoginRepository
import com.friends.ggiriggiri.ui.first.register.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoogleLoginService @Inject constructor(
    private val googleLoginRepository: GoogleLoginRepository
) {
    suspend fun signInWithGoogle(idToken: String): Boolean {
        return googleLoginRepository.signInWithGoogle(idToken)
    }

    // 네이버 로그인 후 Firestore에서 사용자 정보 가져오기
    suspend fun handleGoogleLogin(email: String, userName: String, userProfileImage: String, naverToken: String): UserModel? {
        return try {
            withContext(Dispatchers.IO) {
                val user = googleLoginRepository.getOrCreateUser(email, userName, userProfileImage, naverToken)
                Log.d("GoogleLoginRepository", "Firestore에서 유저 처리 완료: ${user.userId}")
                user
            }
        } catch (e: Exception) {
            Log.e("GoogleLoginRepository", "Firestore 사용자 처리 중 오류 발생", e)
            null
        }
    }
}
