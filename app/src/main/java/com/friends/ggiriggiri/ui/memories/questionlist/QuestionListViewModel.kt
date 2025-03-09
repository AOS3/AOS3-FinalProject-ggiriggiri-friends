package com.friends.ggiriggiri.ui.memories.questionlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.data.model.QuestionListItemModel
import com.friends.ggiriggiri.data.service.QuestionListService2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionListViewModel @Inject constructor(
    private val service: QuestionListService2
): ViewModel(){

    private val _questionList = MutableLiveData<List<QuestionListItemModel>>()
    val questionList: LiveData<List<QuestionListItemModel>> = _questionList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val questions = service.getQuestionList()
            _questionList.postValue(questions)
            _isLoading.postValue(false)
        }
    }

}