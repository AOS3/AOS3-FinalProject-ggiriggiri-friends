package com.friends.ggiriggiri.ui.third.response

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.data.model.ResponseModel
import com.friends.ggiriggiri.data.service.RequestService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ResponseViewModel  @Inject constructor(
    private val requestService: RequestService
) : ViewModel() {

    // 사진 업로드 여부
    private val _isImageUploaded = MutableLiveData(false)
    val isImageUploaded: LiveData<Boolean> = _isImageUploaded

    // 입력된 텍스트 상태
    private val _isTextEntered = MutableLiveData(false)
    val isTextEntered: LiveData<Boolean> = _isTextEntered

    // 응답 버튼 활성화 여부
    private val _isSubmitEnabled = MutableLiveData(false)
    val isSubmitEnabled: LiveData<Boolean> = _isSubmitEnabled

    private val _requestUser = MutableLiveData<String?>()
    val requestUser: LiveData<String?> get() = _requestUser

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

    // 응답 버튼 활성화 상태 업데이트
    private fun updateSubmitButtonState() {
        _isSubmitEnabled.value = _isImageUploaded.value == true && _isTextEntered.value == true
    }

    // 요청한 사람의 정보를 가져오는 함수
    fun fetchRequestUserInfo(documentId: String) {
        requestService.fetchRequestUserInfo(documentId) { userName ->
            _requestUser.value = userName ?: "알 수 없는 사용자"
        }
    }

    fun submitResponse(
        requestId: String,
        userId: String,
        responseMessage: String,
        imageUrl: String?,
        groupDocumentId: String,
        onComplete: (Boolean) -> Unit // 성공 여부 콜백 추가
    ) {
        viewModelScope.launch(Dispatchers.IO) { // 네트워크 작업은 IO에서 실행
            Log.d("ResponseViewModel", "submitResponse 호출됨")
            Log.d("ResponseViewModel", "requestId: $requestId, userId: $userId")
            Log.d("ResponseViewModel", "responseMessage: $responseMessage")
            Log.d("ResponseViewModel", "imageUrl: $imageUrl") // 이미지 URL 확인
            Log.d("ResponseViewModel", "groupDocumentId: $groupDocumentId")

            val response = ResponseModel(
                responseUserDocumentID = userId,
                responseMessage = responseMessage,
                responseImage = imageUrl ?: "",
                responseTime = System.currentTimeMillis()
            )

            val success = requestService.saveResponse(requestId, response, groupDocumentId, imageUrl ?: "")
            withContext(Dispatchers.Main) { // UI 업데이트는 메인 스레드에서 실행
                if (success) {
                    _isSubmitEnabled.value = false // UI에서 버튼 비활성화
                    Log.d("ResponseViewModel", "응답 저장 성공")
                } else {
                    Log.e("ResponseViewModel", "응답 저장 실패")
                }
                onComplete(success) // Fragment에서 처리하도록 콜백 반환
            }
        }
    }

}
