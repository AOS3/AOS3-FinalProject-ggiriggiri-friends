package com.friends.ggiriggiri.ui.first.register

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import com.friends.ggiriggiri.ui.first.login.LoginFragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.concurrent.timer

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val service: UserService
) : ViewModel() {

    //파이어베이스 auto
    var auth =  FirebaseAuth.getInstance()
    private var verificationId: String? = null  // 인증 ID 저장

    // 이름 에러 메시지
    val nameErrorMessage = MutableLiveData<String?>()
    // 이름 유효성 검사 통과여부
    var isNameValid = false

    // 아이디 에러 메시지
    val idErrorMessage = MutableLiveData<String?>()
    // 아이디 에러 색상
    val idErrorColor = MutableLiveData<Int>()
    // 아이디 중복 확인 결과여부
    val isIdValid = MutableLiveData<Boolean>()

    // 비밀번호1 에러 메시지
    val pw1ErrorMessage = MutableLiveData<String?>()
    // 비밀번호2 에러 메시지
    val pw2ErrorMessage = MutableLiveData<String?>()
    // 비밀번호1
    var pw1String:String =""
    // 비밀번호2
    var pw2String:String =""
    // 비밀번호 유효성검사 & 동일 통과여부
    var isPwValid = false

    // 휴대폰 번호 에러 메세지
    val phoneNumberErrorMessage = MutableLiveData<String?>()
    // 휴대폰 번호 유효성 결과
    val isPhoneNumberValid = MutableLiveData<Boolean>()

    // 타이머 표시 텍스트
    var tvFormattedTime = MutableLiveData<String?>()
    // 재인증 요청 텍스트
    var btnRegisterFragmentGetCertificationNumberText = MutableLiveData<String?>()
    // 재인증 버튼 비활성화
    var btnRegisterFragmentGetCertificationNumberEnabled = MutableLiveData<Boolean?>()
    // 인증번호 누르면 인증번호입력창 보이게하는 변수
    val llRegisterFragmentConfirmCertificationIsVisible = MutableLiveData(View.INVISIBLE)
    // 인증번호 누르면 휴대폰번호 입력창 막히게하는 변수
    val etRegisterFragmentPhoneNumberIsEnabled = MutableLiveData(true)
    // 인증완료하면 인증버튼 비활성화
    val btnRegisterFragmentConfirmCertificationNumberEnabled = MutableLiveData(true)

    // 인증번호 에러 메세지
    val certificationNumberErrorMessage = MutableLiveData<String?>()
    // 인증번호 에러 색상
    val certificationNumberErrorColor = MutableLiveData<Int>()
    // 인증 성공시 인증 입력창 막히게하는 변수
    val etRegisterFragmentCertificationNumberIsEnabled = MutableLiveData(true)
    // 인증번호 확인 결과
    val isCertificationNumberValid = MutableLiveData<Boolean>()

    // 가입하기 버튼 클릭
    fun btnRegisterFragmentSignupLoginOnClick():Boolean {

        // 이름, 아이디, 비밀번호, 인증통과 중 하나라도 false일때
        if (!isNameValid || isIdValid.value != true || !isPwValid || isCertificationNumberValid.value != true){
            return false
        }// 모두통과
        else{
            return true
        }
    }

    fun btnRegisterFragmentDuplicationCheckOnClick(loginActivity: LoginActivity, id: String) {
        if (id.isBlank()) {
            idErrorMessage.value = "아이디를 입력해 주세요"
            isIdValid.value = false
            return
        }

        if (id.length < 4 || id.length > 20) {
            idErrorMessage.value = "아이디를 4자 이상 20자 이하로 입력해주세요"
            isIdValid.value = false
            return
        }

        viewModelScope.launch {
            val progressDialog = withContext(Dispatchers.Main) {
                CustomDialogProgressbar(loginActivity).apply { show() }  // 프로그레스바 표시
            }

            try {
                val idValid = service.duplicationCheckUserId(id)  // 비동기 호출

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()  // 결과 받으면 프로그레스바 닫기

                    Log.d("test", idValid.toString())  // 호출 후 출력
                    if (idValid) {
                        idErrorMessage.value = null
                        isIdValid.value = true
                    } else {
                        idErrorMessage.value = "아이디 사용 불가, 다른 아이디를 사용해주세요"
                        isIdValid.value = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()  // 오류 시에도 프로그레스바 닫기
                    idErrorMessage.value = "아이디 중복 확인 중 오류가 발생했습니다."
                    isIdValid.value = false
                }
            }
        }
    }



    fun registerUser(loginActivity: LoginActivity, userModel: UserModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val progressDialog = withContext(Dispatchers.Main) {
                    CustomDialogProgressbar(loginActivity).apply { show() }
                }
                service.addUser(userModel)

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(loginActivity, "회원 가입 완료!", Toast.LENGTH_LONG).show()
                    loginActivity.removeFragment(LoginFragmentName.REGISTER_FRAGMENT)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("회원가입실패", "${e.message}")
                }
            }
        }
    }


    // 인증 번호 받기 버튼
    fun btnRegisterFragmentGetCertificationNumberOnClick(loginActivity: LoginActivity,phoneNumber:String){

        //전화번호가 11자리면 인증번호 전송
        if (isPhoneNumberValid.value == true){

            // 인증 코드 전송 메서드
            sendCertificationCode(loginActivity,phoneNumber)

            // 번호입력창 수정못하게
            etRegisterFragmentPhoneNumberIsEnabled.value = false

            // 인증번호 입력창 보이게
            llRegisterFragmentConfirmCertificationIsVisible.value = View.VISIBLE

            // 버튼이름 바꾸기
            btnRegisterFragmentGetCertificationNumberText.value = "재인증"

            // 1분 타이머 (60초 = 60000ms)
            val timer = object : CountDownTimer(60000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // 남은 시간을 분:초 형식으로 변환
                    val seconds = (millisUntilFinished / 1000) % 60
                    val minutes = (millisUntilFinished / 1000) / 60
                    val formattedTime = String.format("%02d:%02d", minutes, seconds)

                    // TextView 업데이트
                    tvFormattedTime.value = formattedTime
                }

                override fun onFinish() {
                    if (isCertificationNumberValid.value == false || isCertificationNumberValid.value == null){
                        tvFormattedTime.value = "00:00"
                        btnRegisterFragmentGetCertificationNumberEnabled.value = true
                        etRegisterFragmentPhoneNumberIsEnabled.value = true
                    }else{
                        btnRegisterFragmentGetCertificationNumberEnabled.value = false
                        etRegisterFragmentPhoneNumberIsEnabled.value = false
                    }
                }
            }
            timer.start()
            btnRegisterFragmentGetCertificationNumberEnabled.value = false
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
                this@RegisterViewModel.verificationId = verificationId  // 인증 ID 저장
                Toast.makeText(loginActivity, "인증 코드가 전송되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        val formattedPhoneNumber = "+82" + phoneNumber.substring(1)
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formattedPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(loginActivity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // 인증 코드 확인 메서드
    fun signInWithPhoneAuthCredential(loginActivity: LoginActivity, credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(loginActivity) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(loginActivity, "인증 성공", Toast.LENGTH_SHORT).show()

                    // 인증 성공 시 LiveData 업데이트
                    isCertificationNumberValid.value = true

                    // 인증 입력창 비활성화
                    etRegisterFragmentCertificationNumberIsEnabled.value = false
                    etRegisterFragmentPhoneNumberIsEnabled.value = false
                    btnRegisterFragmentGetCertificationNumberEnabled.value = false

                    // 인증 성공 시 btnRegisterFragmentConfirmCertificationNumberOnclick 호출
                    btnRegisterFragmentConfirmCertificationNumberOnclick("")
                } else {
                    Toast.makeText(loginActivity, "인증코드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    isCertificationNumberValid.value = false
                }
            }
    }




    // 이름입력창 TextWatcher
    fun etRegisterFragmentNameWatcher(name:String){
        if (name.isBlank()) {
            nameErrorMessage.value = "이름을 입력해 주세요."
        } else if (name.length < 2 || name.length > 10) {
            nameErrorMessage.value = "이름은 2자 이상 10자 이하로 입력해야 합니다."
        } else {
            nameErrorMessage.value = null
            isNameValid = true
        }
    }

    // 아이디 중복검사를 통과했는데 다시 아이디를바꿀경우 중복검사결과는 false로..(TextWatcher)
    fun etRegisterFragmentIdWatcher(){
        isIdValid.value = false
    }

    // 비밀번호1 유효성 검사 TextWatcher
    fun etRegisterFragmentPw1Watcher(pw1:String){
        pw1String = pw1
        if (pw1.isEmpty()) {
            pw1ErrorMessage.value = "비밀번호를 입력해 주세요."
        } else if (pw1.length < 4 || pw1.length > 20) {
            pw1ErrorMessage.value = "비밀번호는 4자 이상 20자 이하로 입력해야 합니다."
        } else {
            pw1ErrorMessage.value = null
            isPw12Equal()
        }
    }

    // 비밀번호2 유효성 검사 TextWatcher
    fun etRegisterFragmentPw2Watcher(pw2:String){
        pw2String = pw2
        if (pw2.isEmpty()) {
            pw2ErrorMessage.value = "비밀번호를 입력해 주세요."
        } else if (pw2.length < 4 || pw2.length > 20) {
            pw2ErrorMessage.value = "비밀번호는 4자 이상 20자 이하로 입력해야 합니다."
        } else {
            pw2ErrorMessage.value = null
            isPw12Equal()
        }
    }

    // 비밀번호가 유효성검사를 통과하고 동일한지
    fun isPw12Equal(){
        if (pw1ErrorMessage.value == null && pw2ErrorMessage.value == null && pw1String == pw2String){
            isPwValid = true
        }
    }

    // 전화번호 유효성 검사 TextWatcher
    fun etRegisterFragmentPhoneNumberWatcher(phoneNumber:String){
        if (phoneNumber.length != 11){
            phoneNumberErrorMessage.value = "휴대폰번호 11자리를 입력해주세요"
            isPhoneNumberValid.value = false
        }else{
            phoneNumberErrorMessage.value = null
            isPhoneNumberValid.value = true
        }
    }

    // 인증번호 유효성 검사
    fun btnRegisterFragmentConfirmCertificationNumberOnclick(certificationNumber: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId ?: "", certificationNumber)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isCertificationNumberValid.value = true
                    Toast.makeText(auth.app.applicationContext, "인증 성공", Toast.LENGTH_SHORT).show()

                    // 인증 입력창 비활성화
                    etRegisterFragmentCertificationNumberIsEnabled.value = false
                    etRegisterFragmentPhoneNumberIsEnabled.value = false
                    btnRegisterFragmentGetCertificationNumberEnabled.value = false
                    btnRegisterFragmentConfirmCertificationNumberEnabled.value = false
                } else {
                    isCertificationNumberValid.value = false
                    Toast.makeText(auth.app.applicationContext, "인증 실패. 번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
    }






}
