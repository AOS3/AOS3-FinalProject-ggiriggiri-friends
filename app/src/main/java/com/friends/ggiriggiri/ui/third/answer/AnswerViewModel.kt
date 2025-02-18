package com.friends.ggiriggiri.ui.third.answer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnswerViewModel @Inject constructor() : ViewModel() {

    private val _questionText = MutableLiveData<String>()
    val questionText: LiveData<String> get() = _questionText

    private val _questionImageUrl = MutableLiveData<String?>()
    val questionImageUrl: LiveData<String?> get() = _questionImageUrl

    fun setQuestionText(text: String) {
        _questionText.value = text
    }

    fun setQuestionImageUrl(imageUrl: String?) {
        _questionImageUrl.value = imageUrl
    }
}
