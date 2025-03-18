package com.friends.ggiriggiri.data.service

import android.content.SharedPreferences
import android.util.Log
import com.friends.ggiriggiri.data.repository.KakaoLoginRepository
import com.friends.ggiriggiri.ui.start.register.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KakaoLoginService @Inject constructor(
    private val repository: KakaoLoginRepository,
    private val sharedPreferences: SharedPreferences
) {

    // 카카오 로그인 후 Firestore에서 사용자 정보 가져오기
    suspend fun handleKakaoLogin(email: String, userName: String, userProfileImage: String, kakaoToken: String): UserModel? {
        return try {
            withContext(Dispatchers.IO) {
                val user = repository.getOrCreateUser(email, userName, userProfileImage, kakaoToken)
                Log.d("KakaoLoginService", "Firestore에서 유저 처리 완료: ${user.userId}")

                saveAutoLoginToken(user.userAutoLoginToken)

                user
            }
        } catch (e: Exception) {
            Log.e("KakaoLoginService", "Firestore 사용자 처리 중 오류 발생", e)
            null
        }
    }

    // 자동 로그인 토큰을 SharedPreferences에 저장
    private fun saveAutoLoginToken(token: String) {
        sharedPreferences.edit().putString("autoLoginToken", token).apply()
    }

    //Firestore에서 사용자 조회 후 탈퇴한 회원이면 로그인x
    suspend fun userCancelMembershipCheck(email: String): Boolean {
        return repository.userCancelMembershipCheck(email)
    }
}