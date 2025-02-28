package com.friends.ggiriggiri.data.service

import android.content.SharedPreferences
import android.util.Log
import com.friends.ggiriggiri.data.repository.NaverLoginRepository
import com.friends.ggiriggiri.ui.first.register.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NaverLoginService(private val repository: NaverLoginRepository, private val sharedPreferences: SharedPreferences) {

    // 네이버 로그인 후 Firestore에서 사용자 정보 가져오기
    suspend fun handleNaverLogin(email: String, userName: String, userProfileImage: String, naverToken: String): UserModel? {
        return try {
            withContext(Dispatchers.IO) {
                val user = repository.getOrCreateUser(email, userName, userProfileImage, naverToken)
                Log.d("NaverLoginRepository", "Firestore에서 유저 처리 완료: ${user.userId}")

                saveAutoLoginToken(user.userAutoLoginToken)

                user
            }
        } catch (e: Exception) {
            Log.e("NaverLoginRepository", "Firestore 사용자 처리 중 오류 발생", e)
            null
        }
    }

    suspend fun getUserByAutoLoginToken(token: String): UserModel? {
        return repository.getUserByAutoLoginToken(token)
    }

    private fun saveAutoLoginToken(token: String) {
        sharedPreferences.edit().putString("autoLoginToken", token).apply()
    }

}