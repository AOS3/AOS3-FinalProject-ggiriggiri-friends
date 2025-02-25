package com.friends.ggiriggiri.ui.fifth.requestlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.service.RequestListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestListViewModel @Inject constructor(
    application: Application,
    private val service: RequestListService
) : AndroidViewModel(application) {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _requestList = MutableLiveData<List<RequestModel>>()
    val requestList: LiveData<List<RequestModel>> = _requestList

    private val userGroupDocumentID: String =
        (application as App).loginUserModel.userGroupDocumentID

    init {
        loadRequests()
    }

    private fun loadRequests() {

        viewModelScope.launch {
            _isLoading.postValue(true)

            val requests = service.getRequestList(userGroupDocumentID)
            _requestList.postValue(requests)

            _isLoading.postValue(false)
        }

    }
}