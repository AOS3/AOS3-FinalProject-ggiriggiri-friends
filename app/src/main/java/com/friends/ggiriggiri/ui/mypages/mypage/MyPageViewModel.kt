package com.friends.ggiriggiri.ui.mypages.mypage

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.BuildConfig
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.data.service.MyPageService
import com.friends.ggiriggiri.ui.start.register.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    val service: MyPageService,
    val userService: UserService
) : ViewModel() {
    // 사용자 문서 ID
    var userDocumentId: String? = null
    // 사용자 이름
    val userName = MutableLiveData("")
    // 그룹명
    val groupName = MutableLiveData("")
    // 프로필 이미지 URI를 저장하는 LiveData
    private val _profileImageUri = MutableLiveData<Uri?>()
    val profileImageUri: LiveData<Uri?> = _profileImageUri
    // 소셜로그인
    private val _isSocialLogin = MutableLiveData<Boolean>()
    val isSocialLogin: LiveData<Boolean> get() = _isSocialLogin



    // 소셜 로그인 여부 확인
    fun checkSocialLogin() {
        val documentId = userDocumentId ?: return
        viewModelScope.launch {
            try {
                _isSocialLogin.value = service.isSocialLoginUser(documentId)
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "소셜 로그인 여부 확인 실패: ${e.message}")
                _isSocialLogin.value = false // 기본값 설정
            }
        }
    }


    // 사용자 데이터 가져오기 및 UI 업데이트
    fun gettingUserAndGroupName() {
        val documentId = userDocumentId ?: return
        viewModelScope.launch {
            try {
                // 사용자 데이터 가져오기
                val user = service.selectUserDataByUserDocumentIdOne(documentId)
                userName.value = user.userName


                if (!user.userProfileImage.isNullOrEmpty()) {
                    _profileImageUri.value = Uri.parse(user.userProfileImage)
                } else {
                    _profileImageUri.value = getDefaultProfileImageUri()
                }

                if (!user.userGroupDocumentID.isNullOrEmpty()) {
                    groupName.value = service.getGroupName(user.userGroupDocumentID)
                } else {
                    groupName.value = "소속된 그룹 없음"
                }
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "사용자 데이터 가져오기 실패: ${e.message}")
            }
        }
    }

    // Firestore의 userProfileImage 필드만 업데이트하여 프로필 이미지 변경
    fun updateProfileImage(newImageUrl: String) {
        val documentId = userDocumentId ?: return
        viewModelScope.launch {
            try {
                service.updateProfileImage(documentId, newImageUrl)
                _profileImageUri.value = Uri.parse(newImageUrl)
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "프로필 이미지 업데이트 실패: ${e.message}")
            }
        }
    }

    // 기본 프로필 이미지 URI 반환 함수
    private fun getDefaultProfileImageUri(): Uri {
        return Uri.parse("android.resource://${BuildConfig.APPLICATION_ID}/${R.drawable.ic_default_profile}")
    }

    fun exitGroup(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val documentId = userDocumentId ?: return
        viewModelScope.launch {
            try {
                service.exitGroup(documentId)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun cancelMembership(){
        val documentId = userDocumentId ?: return
        viewModelScope.launch {
            try {
                userService.cancelMembership(documentId)
            }catch (e: Exception){
                Log.d("회원탈퇴 실패",e.toString())
            }
        }
    }
}