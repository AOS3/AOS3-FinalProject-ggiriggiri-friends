package com.friends.ggiriggiri.ui.notification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.friends.ggiriggiri.data.service.AnswerService
import com.google.firebase.firestore.DocumentId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Private

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository,
    private val answerService: AnswerService
) : ViewModel() {

    private val _userNotifications = MutableLiveData<List<NotificationEntity>>()
    val userNotifications: LiveData<List<NotificationEntity>> = _userNotifications

    fun selectUserNotification(userDocumentId: String) {
        viewModelScope.launch {
            repository.getUserNotifications(userDocumentId).collect { notifications ->
                _userNotifications.postValue(notifications)

                Log.d("NotificationViewModel", "Fetched notifications: $notifications")

                notifications.forEach { notification ->
                    Log.d("NotificationViewModel", "Notification - ID: ${notification.userDocumentID}, Title: ${notification.title}, Message: ${notification.message}, Timestamp: ${notification.timestamp}")
                }
            }
        }
    }
}




