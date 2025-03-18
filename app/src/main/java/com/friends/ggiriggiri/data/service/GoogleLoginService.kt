package com.friends.ggiriggiri.data.service

import android.content.SharedPreferences
import android.util.Log
import com.friends.ggiriggiri.data.repository.GoogleLoginRepository
import com.friends.ggiriggiri.ui.start.register.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoogleLoginService @Inject constructor(
    private val googleLoginRepository: GoogleLoginRepository,
    private val sharedPreferences: SharedPreferences
) {
    suspend fun signInWithGoogle(idToken: String): Boolean {
        return googleLoginRepository.signInWithGoogle(idToken)
    }

    // 네이버 로그인 후 Firestore에서 사용자 정보 가져오기
    suspend fun handleGoogleLogin(email: String, userName: String, userProfileImage: String, googleToken: String): UserModel? {
        return try {
            withContext(Dispatchers.IO) {
                val user = googleLoginRepository.getOrCreateUser(email, userName, userProfileImage, googleToken)
                Log.d("GoogleLoginRepository", "Firestore에서 유저 처리 완료: ${user.userId}")

                saveAutoLoginToken(user.userAutoLoginToken)

                user
            }
        } catch (e: Exception) {
            Log.e("GoogleLoginRepository", "Firestore 사용자 처리 중 오류 발생", e)
            null
        }
    }

    private fun saveAutoLoginToken(token: String) {
        sharedPreferences.edit().putString("autoLoginToken", token).apply()
    }

    suspend fun getUserByAutoLoginToken(token: String): UserModel? {
        return googleLoginRepository.getUserByAutoLoginToken(token)
    }

    //Firestore에서 사용자 조회 후 탈퇴한 회원이면 로그인x
    suspend fun userCancelMembershipCheck(email: String): Boolean {
        return googleLoginRepository.userCancelMembershipCheck(email)
    }
}
