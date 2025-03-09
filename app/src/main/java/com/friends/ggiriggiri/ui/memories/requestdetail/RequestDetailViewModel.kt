package com.friends.ggiriggiri.ui.memories.requestdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.service.RequestDetailService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestDetailViewModel @Inject constructor(
    private val service: RequestDetailService
) : ViewModel() {

    private val _requestDetail = MutableLiveData<RequestModel?>()
    val requestDetail: LiveData<RequestModel?> = _requestDetail

    private val _userProfileImageMap = MutableLiveData<Map<String, String>>()
    val userProfileImageMap: LiveData<Map<String, String>> = _userProfileImageMap

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadRequestDetail(requestId: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)

            val request = service.getRequestDetail(requestId)

            val profileImageMap = mutableMapOf<String, String>()
            val deferredList = request?.responseList?.map { response ->
                async {
                    val profileImage = service.getUserProfileImage(response.responseUserDocumentID)
                    profileImageMap[response.responseUserDocumentID] = profileImage
                }
            } ?: emptyList()

            deferredList.awaitAll()
            _userProfileImageMap.postValue(profileImageMap)
            _requestDetail.postValue(request)

            _isLoading.postValue(false)
        }
    }


}