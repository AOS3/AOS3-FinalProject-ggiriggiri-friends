package com.friends.ggiriggiri.ui.third.home

import android.util.Log
import android.util.Printer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.data.model.QuestionListModel
import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.repository.RequestRepository
import com.friends.ggiriggiri.data.service.AnswerService
import com.friends.ggiriggiri.data.service.HomeService
import com.friends.ggiriggiri.data.service.QuestionListService
import com.friends.ggiriggiri.data.service.RequestService
import com.friends.ggiriggiri.data.vo.QuestionListVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Private

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val questionListService: QuestionListService,
    private val requestService: RequestService,
    private val homeService: HomeService,
    private val answerService: AnswerService,
) : ViewModel() {

    private val _groupName = MutableLiveData<String?>()
    val groupName: LiveData<String?> get() = _groupName

    private val _userProfiles = MutableLiveData<List<Pair<String, String>>>()
    val userProfiles: LiveData<List<Pair<String, String>>> get() = _userProfiles

    private val _todayQuestionList = MutableLiveData<QuestionListModel?>()
    val todayQuestionList: LiveData<QuestionListModel?> get() = _todayQuestionList

    private val _question = MutableStateFlow<QuestionListVO?>(null)
    val question: StateFlow<QuestionListVO?> get() = _question

    private val _activeRequests = MutableLiveData<List<RequestModel>>()
    val activeRequests: LiveData<List<RequestModel>> get() = _activeRequests

    private val _latestRequest = MutableLiveData<RequestModel?>()
    val latestRequest: LiveData<RequestModel?> get() = _latestRequest

    private val _isUserAnswered = MutableLiveData<Boolean>()
    val isUserAnswered: LiveData<Boolean> get() = _isUserAnswered

//    init {
//        fetchTodayQuestionList()
//    }

//    fun fetchTodayQuestionList() {
//        viewModelScope.launch {
//            _todayQuestionList.value = questionListService.getTodayQuestionList()
//        }
//    }

    fun loadGroupName(groupId: String) {
        homeService.fetchGroupName(groupId) { name ->
            _groupName.value = name
        }
    }

    fun loadGroupUserProfiles(groupId: String) {
        if (groupId.isNotEmpty()) {
            homeService.fetchGroupUserProfiles(groupId) { profiles ->
                _userProfiles.postValue(profiles)
            }
        }
    }

//    fun fetchTodayQuestionList() {
//        viewModelScope.launch {
//            val response = questionListService.getTodayQuestionList()
//
//            // color 값이 null이거나 빈 문자열이면 기본값 "#000000" 사용
//            val validColor = response?.color?.takeIf { it.isNotEmpty() } ?: "#000000"
//
//            // 기존 객체를 복사하면서 color 값만 수정
//            val updatedResponse = response?.copy(color = validColor)
//
//            _todayQuestionList.value = updatedResponse
//        }
//    }

    fun loadTodayQuestion(groupId: String) {
        viewModelScope.launch {
            val todayQuestion = questionListService.getTodayQuestion(groupId)
            Log.d("HomeViewModel", todayQuestion.toString()) // ✅ 로그 추가
            Log.d("HomeViewModel", "📌 Firestore에서 가져온 질문: $todayQuestion") // ✅ 로그 추가
            _question.value = todayQuestion
        }
    }

    fun checkUserResponseExists(requestId: String, userId: String, onResult: (Boolean) -> Unit) {
        requestService.checkUserResponseExists(requestId, userId, onResult)
    }

    fun checkUserAnswerExists(socialActivity: SocialActivity) {
        viewModelScope.launch {
            val loginUser = (socialActivity.application as App).loginUserModel
            val groupDayFromCreate = answerService.gettingGroupDayFromCreate(loginUser.userGroupDocumentID)
            val questionDataID = answerService.gettingQuestionDocumentIds(loginUser.userGroupDocumentID, groupDayFromCreate)

            Log.d("checkUserAnswerExists","${questionDataID[0]} , ${loginUser.userDocumentId} 홈뷰모델")


            answerService.checkUserAnswerExists(questionDataID[0], loginUser.userDocumentId) { exists ->
                _isUserAnswered.postValue(exists)
                Log.d("checkUserAnswerExists","${exists} 홈뷰모델")
            }
        }
    }

    fun loadLatestRequest(userGroupId: String) {
        Log.d("HomeViewModel", "최신 요청 불러오기 시작")
        requestService.fetchLatestRequest(userGroupId) { request ->
            if (request != null) {
                Log.d("HomeViewModel", "최신 요청 ID: ${request.requestId}, 상태: ${request.requestState}")
            } else {
                Log.d("HomeViewModel", "최신 요청 없음")
            }
            _latestRequest.postValue(request)
        }
    }
}