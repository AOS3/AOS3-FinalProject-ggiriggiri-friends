package com.friends.ggiriggiri.ui.memories.questionanswer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.data.model.QuestionAnswerModel
import com.friends.ggiriggiri.data.model.QuestionListModel
import com.friends.ggiriggiri.data.service.QuestionAnswerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionAnswerViewModel @Inject constructor(
    private val service: QuestionAnswerService
) : ViewModel() {

    private val _questionData = MutableLiveData<QuestionListModel>()
    val questionData: LiveData<QuestionListModel> = _questionData

    private val _answers = MutableLiveData<List<QuestionAnswerModel>>()
    val answers: LiveData<List<QuestionAnswerModel>> = _answers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadQuestionData(questionListDocumentID: String, questionDataDocumentID: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)

            val question = service.getQuestionData(questionListDocumentID)
            val answerList = service.getAnswers(questionDataDocumentID)

            _questionData.postValue(question)
            _answers.postValue(answerList)

            _isLoading.postValue(false)
        }
    }
}