package com.friends.ggiriggiri.ui.first.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

enum class LoginStatus {
    SUCCESS,
    USER_DEACTIVATED,
    ERROR,
}

@HiltViewModel
class LoginViewModel @Inject constructor(

):ViewModel(){

    //아이디 입력요소
    val tilLoginFragmentIdValue = MutableLiveData("")

    //비밀번호 입력요소
    val tilLoginFragmentPwValue = MutableLiveData("")

    // 로그인 결과 값
    val loginResult = MutableLiveData<LoginStatus?>()

    // 로그인 에러 메시지
    val loginErrorMessage = MutableLiveData("")

    // 로그인 버튼
    // btnLoginFragmentLogin - onClick
    fun btnLoginFragmentLoginOnClick(){

    }



}