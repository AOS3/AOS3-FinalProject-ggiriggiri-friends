package com.friends.ggiriggiri.ui.start.register

import com.friends.ggiriggiri.util.UserSocialLoginState
import com.friends.ggiriggiri.util.UserState

class UserModel {
    var userDocumentId:String = ""
    var userId:String = ""
    var userPw:String = ""
    var userName:String = ""
    var userState = UserState.NORMAL
    var userJoinTime:Long = 0L
    var userFcmCode = mutableListOf<String>()
    var userProfileImage:String = ""
    var userPhoneNumber:String = ""
    var userGroupDocumentID:String = ""
    var userSocialLogin = UserSocialLoginState.NOTHING
    var userAutoLoginToken:String = ""
    var userKakaoToken:String = ""
    var userNaverToken:String = ""
    var userGoogleToken:String = ""

    fun toUserVO() :UserVO{
        val userVO = UserVO()

        userVO.userId = userId
        userVO.userPw = userPw
        userVO.userName = userName
        userVO.userState = userState.num
        userVO.userJoinTime =userJoinTime
        userVO.userFcmCode = userFcmCode.toMutableList()
        userVO.userProfileImage = userProfileImage
        userVO.userPhoneNumber = userPhoneNumber
        userVO.userGroupDocumentID = userGroupDocumentID
        userVO.userSocialLogin = userSocialLogin.num
        userVO.userAutoLoginToken = userAutoLoginToken
        userVO.userKakaoToken = userKakaoToken
        userVO.userNaverToken = userNaverToken
        userVO.userGoogleToken = userGoogleToken

        return userVO
    }
}