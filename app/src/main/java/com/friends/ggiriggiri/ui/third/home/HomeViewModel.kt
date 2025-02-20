package com.friends.ggiriggiri.ui.third.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.data.model.QuestionListModel
import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.repository.RequestRepository
import com.friends.ggiriggiri.data.service.QuestionListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val questionListService: QuestionListService,
    private val requestRepository: RequestRepository
) : ViewModel() {

    private val _todayQuestionList = MutableLiveData<QuestionListModel?>()
    val todayQuestionList: LiveData<QuestionListModel?> get() = _todayQuestionList

    private val _activeRequests = MutableLiveData<List<RequestModel>>()
    val activeRequests: LiveData<List<RequestModel>> get() = _activeRequests

    init {
        fetchTodayQuestionList()
    }

    fun fetchTodayQuestionList() {
        viewModelScope.launch {
            _todayQuestionList.value = questionListService.getTodayQuestionList()
        }
    }

    // 활성화된 요청 가져오기
    fun loadActiveRequests(userGroupId: String) {
        requestRepository.getActiveRequests(userGroupId) { requests ->
            _activeRequests.value = requests
        }
    }
}