package com.friends.ggiriggiri.ui.fourth.mypage

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(

) : ViewModel() {

    // 프로필 이미지 URI를 저장하는 LiveData
    val profileImageUri: MutableLiveData<Uri?> = MutableLiveData()

    // 프로필 이미지 URI 설정 함수
    fun setProfileImageUri(uri: Uri) {
        profileImageUri.value = uri
    }
}