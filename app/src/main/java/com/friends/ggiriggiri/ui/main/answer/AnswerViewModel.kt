package com.friends.ggiriggiri.ui.main.answer

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.data.model.AnswerModel
import com.friends.ggiriggiri.data.service.AnswerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnswerViewModel @Inject constructor(
    private val answerService: AnswerService
) : ViewModel() {

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

    fun btnAnswerSubmitOnCLick(socialActivity: SocialActivity, answer: String, onComplete: () -> Unit) {
        if (answer.length < 9) {
            Toast.makeText(socialActivity, "답변을 10글자 이상 입력해주세요!", Toast.LENGTH_SHORT).show()
            onComplete() // 글자 수 부족 시 프로그래스바 닫기
        } else {
            val loginUser = (socialActivity.application as App).loginUserModel
            val answerModel = AnswerModel().apply {
                answerMessage = answer
                answerUserDocumentID = loginUser.userDocumentId
                answerResponseTime = System.currentTimeMillis()
            }

            viewModelScope.launch {
                try {
                    val groupDayFromCreate = answerService.gettingGroupDayFromCreate(loginUser.userGroupDocumentID)
                    val questionDataID = answerService.gettingQuestionDocumentIds(loginUser.userGroupDocumentID, groupDayFromCreate)
                    answerService.addAnswer(questionDataID[0], answerModel)
                    socialActivity.onBackPressedDispatcher.onBackPressed()
                    Toast.makeText(socialActivity,"답변을 완료했습니다!",Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(socialActivity, "답변 제출 중 오류 발생: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                } finally {
                    onComplete() // 네트워크 요청 완료 후 프로그래스바 닫기
                }
            }
        }
    }

}
