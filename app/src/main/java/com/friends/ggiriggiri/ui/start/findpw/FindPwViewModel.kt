package com.friends.ggiriggiri.ui.start.findpw

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import com.friends.ggiriggiri.ui.start.register.UserService
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class FindPwViewModel @Inject constructor(
    private val service: UserService
) : ViewModel() {
    //파이어베이스 auto
    var auth =  FirebaseAuth.getInstance()
    private var verificationId: String? = null  // 인증 ID 저장

    // 아이디 에러 메시지
    val idErrorMessage = MutableLiveData<String?>()

    // 휴대폰 번호 에러 메세지
    val phoneNumberErrorMessage = MutableLiveData<String?>()

    // 휴대폰 번호 유효성 결과
    var isPhoneNumberValid = MutableLiveData<Boolean>()

    // 타이머 표시 텍스트
    var tvFindPwFormattedTimeText = MutableLiveData<String?>()

    // 재인증 요청 텍스트
    var btnFindPwFragmentGetCertificationNumberText = MutableLiveData<String?>()

    // 재인증 버튼 비활성화
    var btnFindPwFragmentGetCertificationNumberEnabled = MutableLiveData<Boolean?>()

    // 인증번호 누르면 인증번호입력창 보이게하는 변수
    var llFindPwFragmentConfirmCertificationIsVisible = MutableLiveData(View.INVISIBLE)

    // 인증번호 누르면 휴대폰번호 입력창 막히게하는 변수
    var etFindPwFragmentPhoneNumberIsEnabled = MutableLiveData(true)

    // 인증완료하면 인증버튼 비활성화
    var btnFindPwFragmentConfirmCertificationNumberEnabled = MutableLiveData(true)

    // 인증번호 에러 메세지
    var certificationNumberErrorMessage = MutableLiveData<String?>()

    // 인증번호 에러 색상
    var certificationNumberErrorColor = MutableLiveData<Int>()

    // 인증 성공시 인증 입력창 막히게하는 변수
    var etFindPwFragmentCertificationNumberIsEnabled = MutableLiveData(true)

    // 인증번호 확인 결과
    val isCertificationNumberValid = MutableLiveData<Boolean>()

    // 아이디, 전화번호 입력확인
    var isValid = true

    // 비밀번호 재설정ㄱㄱ
    var canWeFindPw = MutableLiveData(false)

    // 전화번호 입력 Watcher
    fun etFindPwFragmentPhoneNumberWatcher(phoneNumber: String) {
        if (phoneNumber.length != 11){
            phoneNumberErrorMessage.value = "휴대폰번호 11자리를 입력해주세요"
            isPhoneNumberValid.value = false
        }else{
            phoneNumberErrorMessage.value = null
            isPhoneNumberValid.value = true
        }
    }

    // 인증 번호 받기 버튼
    fun btnFindPwFragmentGetCertificationNumberOnClick(loginActivity: LoginActivity, phoneNumber:String){
        //전화번호가 11자리면 인증번호 전송
        if (isPhoneNumberValid.value == true){

            // 인증 코드 전송 메서드
            sendCertificationCode(loginActivity,phoneNumber)

            // 번호입력창 수정못하게
            etFindPwFragmentPhoneNumberIsEnabled.value = false

            // 인증번호 입력창 보이게
            llFindPwFragmentConfirmCertificationIsVisible.value = View.VISIBLE

            // 버튼 이름 바꾸기
            btnFindPwFragmentGetCertificationNumberText.value = "재인증"

            // 1분 타이머 (60초 = 60000ms)
            val timer = object : CountDownTimer(120000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // 남은 시간을 분:초 형식으로 변환
                    val seconds = (millisUntilFinished / 1000) % 60
                    val minutes = (millisUntilFinished / 1000) / 60
                    val formattedTime = String.format("%02d:%02d", minutes, seconds)

                    // TextView 업데이트
                    tvFindPwFormattedTimeText.value = formattedTime
                }

                override fun onFinish() {
                    if (isCertificationNumberValid.value == false || isCertificationNumberValid.value == null){
                        tvFindPwFormattedTimeText.value = "00:00"
                        btnFindPwFragmentGetCertificationNumberEnabled.value = true
                        etFindPwFragmentPhoneNumberIsEnabled.value = true
                    }else{
                        btnFindPwFragmentGetCertificationNumberEnabled.value = false
                        etFindPwFragmentPhoneNumberIsEnabled.value = false
                    }
                }
            }
            timer.start()
            btnFindPwFragmentGetCertificationNumberEnabled.value = false
        }
    }
    // 인증 코드 전송 메서드
    fun sendCertificationCode(loginActivity: LoginActivity, phoneNumber: String) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // 자동 인증 시 처리 생략 (수동 확인만 사용)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(loginActivity, "인증 실패.", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                this@FindPwViewModel.verificationId = verificationId  // 인증 ID 저장
                Toast.makeText(loginActivity, "인증 코드가 전송되었습니다.", Toast.LENGTH_SHORT).show()
                btnFindPwFragmentConfirmCertificationNumberEnabled.value = true
            }
        }

        val formattedPhoneNumber = "+82" + phoneNumber.substring(1)
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formattedPhoneNumber)
            .setTimeout(120L, TimeUnit.SECONDS)
            .setActivity(loginActivity)
            .setCallbacks(callbacks)
            .build()

        btnFindPwFragmentConfirmCertificationNumberEnabled.value = false

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // 인증번호 유효성 검사
    fun btnFindPwFragmentConfirmCertificationNumberOnclick(certificationNumber: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId ?: "", certificationNumber)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isCertificationNumberValid.value = true
                    Toast.makeText(auth.app.applicationContext, "인증 성공", Toast.LENGTH_SHORT).show()

                    // 인증 입력창 비활성화
                    etFindPwFragmentCertificationNumberIsEnabled.value = false
                    etFindPwFragmentPhoneNumberIsEnabled.value = false
                    btnFindPwFragmentGetCertificationNumberEnabled.value = false
                    btnFindPwFragmentConfirmCertificationNumberEnabled.value = false
                } else {
                    isCertificationNumberValid.value = false
                    Toast.makeText(auth.app.applicationContext, "인증 실패. 번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //비밀번호 재설정 버튼 클릭
    fun btnFindPwFragmentResetPwOnClick(id:String,phoneNumber: String){
        isValid = true

        //아이디 유효성 검사
        if (id.isBlank()) {
            idErrorMessage.value = "아이디를 입력해 주세요."
            isValid = false
        } else {
            idErrorMessage.value = null
        }

        // 인증번호 통과여부
        if (isCertificationNumberValid.value != true){
            isValid = false
        }

        // 모두ok면 아이디찾기진행
        if(isValid){
            canWeFindPw.value = true
            Log.d("canWeFindIdResult","canWeFindIdResult")
        }
    }

    // 비밀번호 재설정으로 이동
    fun goToResetPw(loginActivity: LoginActivity, id: String, phoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val progressDialog = withContext(Dispatchers.Main) {
                CustomDialogProgressbar(loginActivity).apply { show() }
            }
            try {
                val user = service.findUserByIdAndPhoneNumber(id, phoneNumber)
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if (user == null) {
                        // user가 null이면 사용자 없음
                        Toast.makeText(loginActivity, "아이디 또는 전화번호가 잘못되었습니다.", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        // 사용자 정보가 존재할 때 실행할 코드
                        val datBundle = Bundle()
                        datBundle.putString("userDocumentId",user.userDocumentId)
                        loginActivity.replaceFragment(LoginFragmentName.RESET_PW_FRAGMENT,true,true,datBundle)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(loginActivity, "오류가 발생했습니다: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}