package com.friends.ggiriggiri.ui.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE userDocumentId = :userDocumentId ORDER BY timestamp DESC")
    fun getUserNotifications(userDocumentId: String): Flow<List<NotificationEntity>>

    @Query("DELETE FROM notifications WHERE userDocumentId = :userDocumentId")
    suspend fun clearUserNotifications(userDocumentId: String)
}

