package com.friends.ggiriggiri.ui.third.request

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RequestViewModel : ViewModel() {

    // 사진 업로드 여부
    private val _isImageUploaded = MutableLiveData(false)
    val isImageUploaded: LiveData<Boolean> = _isImageUploaded

    // 입력된 텍스트 상태
    private val _isTextEntered = MutableLiveData(false)
    val isTextEntered: LiveData<Boolean> = _isTextEntered

    // 요청 버튼 활성화 여부
    private val _isSubmitEnabled = MutableLiveData(false)
    val isSubmitEnabled: LiveData<Boolean> = _isSubmitEnabled

    // 사진 업로드 상태 업데이트
    fun setImageUploaded(isUploaded: Boolean) {
        _isImageUploaded.value = isUploaded
        updateSubmitButtonState()
    }

    // 텍스트 입력 상태 업데이트
    fun setTextEntered(isEntered: Boolean) {
        _isTextEntered.value = isEntered
        updateSubmitButtonState()
    }

    // 요청 버튼 활성화 상태 업데이트
    private fun updateSubmitButtonState() {
        _isSubmitEnabled.value = _isImageUploaded.value == true && _isTextEntered.value == true
    }
}
