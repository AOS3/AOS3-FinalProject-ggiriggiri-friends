package com.friends.ggiriggiri.ui.first.findpw

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.friends.ggiriggiri.ui.first.register.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FindPwViewModel @Inject constructor(
    private val service: UserService
) : ViewModel() {

    // 휴대폰 번호 에러 메세지
    val phoneNumberErrorMessage = MutableLiveData<String?>()
    // 휴대폰 번호 유효성 결과
    val isPhoneNumberValid = MutableLiveData<Boolean>()



    fun etFindPwFragmentPhoneNumberWatcher(phoneNumber: String) {
        if (phoneNumber.length != 11){
            phoneNumberErrorMessage.value = "휴대폰번호 11자리를 입력해주세요"
            isPhoneNumberValid.value = false
        }else{
            phoneNumberErrorMessage.value = null
            isPhoneNumberValid.value = true
        }
    }

}