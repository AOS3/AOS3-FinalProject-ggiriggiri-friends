package com.friends.ggiriggiri.ui.third.home

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.data.service.AnswerService
import com.friends.ggiriggiri.data.service.HomeService
import com.friends.ggiriggiri.data.service.QuestionListService
import com.friends.ggiriggiri.data.service.RequestService
import com.friends.ggiriggiri.data.vo.QuestionListVO
import com.friends.ggiriggiri.ui.fifth.questionanswer.QuestionAnswerFragment
import com.friends.ggiriggiri.ui.fifth.requestdetail.RequestDetailFragment
import com.friends.ggiriggiri.ui.third.request.RequestFragment
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
    private val answerService: AnswerService
) : ViewModel() {

    private val _groupName = MutableLiveData<String?>()
    val groupName: LiveData<String?> get() = _groupName

    private val _userProfiles = MutableLiveData<List<Pair<String, String>>>()
    val userProfiles: LiveData<List<Pair<String, String>>> get() = _userProfiles

    private val _question = MutableStateFlow<QuestionListVO?>(null)
    val question: StateFlow<QuestionListVO?> get() = _question

    private val _latestRequest = MutableLiveData<RequestModel?>()
    val latestRequest: LiveData<RequestModel?> get() = _latestRequest

    private val _galleryImages = MutableStateFlow<List<String>>(emptyList())
    val galleryImages: StateFlow<List<String>> get() = _galleryImages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isAllDataLoaded = MutableLiveData(false)
    val isAllDataLoaded: LiveData<Boolean> get() = _isAllDataLoaded

    private val _isUserAnswered = MutableLiveData<Boolean>()
    val isUserAnswered: LiveData<Boolean> get() = _isUserAnswered

    private var loadedDataCount = 0
    private val totalDataCount = 6

    var questionDataIDResult = MutableLiveData<String>()
    var groupDayFromCreateResult = MutableLiveData<String>()

    private fun checkAllDataLoaded() {
        loadedDataCount++
        if (loadedDataCount == totalDataCount) {
            _isLoading.postValue(false)
            _isAllDataLoaded.postValue(true)
        }
    }

    fun resetLoadingState() {
        if (_isLoading.value == true) return

        _isLoading.postValue(true)
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            _isLoading.postValue(false)
        }
    }

    fun loadGroupName(groupId: String) {
        _isLoading.postValue(true)
        homeService.fetchGroupName(groupId) { name ->
            _groupName.postValue(name)
            checkAllDataLoaded()
        }
    }

    fun loadGroupUserProfiles(groupId: String) {
        homeService.fetchGroupUserProfiles(groupId) { profiles ->
            _userProfiles.postValue(profiles)
            checkAllDataLoaded()
        }
    }

    fun loadTodayQuestion(groupId: String) {
        viewModelScope.launch {
            val todayQuestion = questionListService.getTodayQuestion(groupId)
            _question.value = todayQuestion
            checkAllDataLoaded()
        }
    }

    fun checkUserResponseExists(requestId: String, userId: String, onResult: (Boolean) -> Unit) {
        requestService.checkUserResponseExists(requestId, userId, onResult)
    }

    fun loadLatestRequest(userGroupId: String) {
        requestService.fetchLatestRequest(userGroupId) { request ->
            _latestRequest.postValue(request)
            checkAllDataLoaded()
        }
    }

    fun loadGalleryImages(groupId: String) {
        viewModelScope.launch {
            val images = homeService.fetchRandomGalleryImages(groupId)
            _galleryImages.value = images
            checkAllDataLoaded()
        }
    }

    fun hasUserRequestedToday(userId: String, groupId: String, onResult: (Boolean) -> Unit) {
        requestService.hasUserRequestedToday(userId, groupId, onResult)
    }

    fun checkUserAnswerExists(userID:String,userGroupID:String) {
        viewModelScope.launch {
            val groupDayFromCreate = answerService.gettingGroupDayFromCreate(userGroupID)
            val questionDataID = answerService.gettingQuestionDocumentIds(userGroupID, groupDayFromCreate)

            questionDataIDResult.value = questionDataID[0]
            groupDayFromCreateResult.value = groupDayFromCreate

            Log.d("checkUserAnswerExists","${questionDataIDResult.value} , ${groupDayFromCreateResult.value} 홈뷰모델")


            answerService.checkUserAnswerExists(questionDataID[0], userID) { exists ->
                _isUserAnswered.postValue(exists)
                checkAllDataLoaded()
                Log.d("checkUserAnswerExists","${exists} 홈뷰모델")
            }
        }
    }

    fun moveToQuestionAndAnswer(socialActivity: SocialActivity,groupDayFromCreateResult:String,questionDataIDResult:String){
        socialActivity?.replaceFragment(QuestionAnswerFragment.newInstance(groupDayFromCreateResult,questionDataIDResult))
    }

}