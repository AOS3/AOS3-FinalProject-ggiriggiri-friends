package com.friends.ggiriggiri.ui.first.register

import com.friends.ggiriggiri.util.UserSocialLoginState
import com.friends.ggiriggiri.util.UserState

class UserVO {
    var userId:String = ""
    var userPw:String = ""
    var userName:String = ""
    var userState = 0
    var userJoinTime:Long = 0L
    var userFcmCode = mutableListOf<String>()
    var userProfileImage:String = ""
    var userPhoneNumber:String = ""
    var userGroupDocumentID:String = ""
    var userSocialLogin = 0
    var userAutoLoginToken:String = ""
    var userKakaoToken:String = ""
    var userNaverToken:String = ""
    var userGoogleToken:String = ""

    fun toUserModel(userDocumentId:String) : UserModel{
        val userModel = UserModel()

        userModel.userDocumentId = userDocumentId
        userModel.userId = userId
        userModel.userPw = userPw
        userModel.userName = userName

        when(userState){
            UserState.NORMAL.num -> userModel.userState = UserState.NORMAL
            UserState.WITHDRAW.num -> userModel.userState = UserState.WITHDRAW
        }

        userModel.userJoinTime =userJoinTime
        userModel.userFcmCode = userFcmCode.toMutableList()
        userModel.userProfileImage = userProfileImage
        userModel.userPhoneNumber = userPhoneNumber
        userModel.userGroupDocumentID = userGroupDocumentID

        when(userSocialLogin){
            UserSocialLoginState.NOTHING.num -> userModel.userSocialLogin = UserSocialLoginState.NOTHING
            UserSocialLoginState.KAKAO.num -> userModel.userSocialLogin = UserSocialLoginState.KAKAO
            UserSocialLoginState.NAVER.num -> userModel.userSocialLogin = UserSocialLoginState.NAVER
            UserSocialLoginState.GOOGLE.num -> userModel.userSocialLogin = UserSocialLoginState.GOOGLE
        }

        userModel.userAutoLoginToken = userAutoLoginToken
        userModel.userKakaoToken = userKakaoToken
        userModel.userNaverToken = userNaverToken
        userModel.userGoogleToken = userGoogleToken

        return userModel

    }
}