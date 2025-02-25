package com.friends.ggiriggiri.ui.first.register

import com.friends.ggiriggiri.App
import javax.inject.Inject

class UserService @Inject constructor() {

    // 회원가입(유저추가)
    suspend fun addUser(userModel: UserModel) {
        val userVO = userModel.toUserVO()
        UserRepository.addUser(userVO)
    }

    // 사용자 아이디와 동일한 사용자의 정보 하나를 반환하는 메서드
    suspend fun login(userId: String, userPw: String): UserModel? {
        val tempMap = UserRepository.login(userId, userPw)
            ?: return null  // 레포지토리에서 null이 반환되면 그대로 null 반환함
        val loginUserVo = tempMap["user_vo"] as? UserVO ?: return null
        val loginUserDocumentId = tempMap["user_document_id"] as? String ?: return null

        return loginUserVo.toUserModel(loginUserDocumentId)
    }

    // 아이디 중복체크(true: 사용가능,false: 사용불가능)
    suspend fun duplicationCheckUserId(userId: String): Boolean {
        val result = UserRepository.duplicationCheckUserId(userId)
        return result
    }

    // 아이디 찾기
    suspend fun findId(userName: String, userPhoneNumber: String): String {
        val result = UserRepository.findId(userName,userPhoneNumber)
        return result
    }

    // 아이디와 전화번호로 해당유저가 있는지 검색하기(FindPwFragment)
    suspend fun findUserByIdAndPhoneNumber(userId: String, userPhoneNumber: String): UserModel? {
        val result = UserRepository.findUserByIdAndPhoneNumber(userId, userPhoneNumber)

        return if (!result.isNullOrEmpty()) {
            val document = result.entries.first()
            val userVO = document.value
            userVO?.toUserModel(document.key)
        } else {
            null
        }
    }

    // 비밀번호 변경
    fun resetUserPw(userDocumentId: String, pw: String) {
        UserRepository.resetUserPw(userDocumentId,pw)
    }
}
