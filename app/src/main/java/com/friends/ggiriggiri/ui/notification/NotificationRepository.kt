package com.friends.ggiriggiri.ui.notification

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {
    fun getUserNotifications(userID: String): Flow<List<NotificationEntity>> {
        return notificationDao.getUserNotifications(userID)
    }

    suspend fun insert(notification: NotificationEntity) {
        notificationDao.insert(notification)
    }

    suspend fun clearUserNotifications(userID: String) {
        notificationDao.clearUserNotifications(userID)
    }
}

