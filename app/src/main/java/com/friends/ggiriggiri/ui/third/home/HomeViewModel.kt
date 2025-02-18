package com.friends.ggiriggiri.ui.third.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.data.model.QuestionListModel
import com.friends.ggiriggiri.data.service.QuestionListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val service: QuestionListService
) : ViewModel() {

    private val _todayQuestionList = MutableLiveData<QuestionListModel?>()
    val todayQuestionList: LiveData<QuestionListModel?> get() = _todayQuestionList

    init {
        fetchTodayQuestionList()
    }

    fun fetchTodayQuestionList() {
        viewModelScope.launch {
            _todayQuestionList.value = service.getTodayQuestionList()
        }
    }
}