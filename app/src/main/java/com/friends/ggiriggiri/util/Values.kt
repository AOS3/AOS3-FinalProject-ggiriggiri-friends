package com.friends.ggiriggiri.util

//유저 상태값
enum class UserState(val num: Int, val str:String){
    NORMAL(1,"정상"),
    WITHDRAW(2,"탈퇴")
}

//유저 소셜 로그인
enum class UserSocialLoginState(val num: Int, val str:String){
    NOTHING(1,"없음"),
    KAKAO(2,"카카오"),
    NAVER(3,"네이버"),
    GOOGLE(4,"구글"),
}