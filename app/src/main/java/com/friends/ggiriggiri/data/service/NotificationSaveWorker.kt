package com.friends.ggiriggiri.data.service

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.friends.ggiriggiri.ui.notification.NotificationDatabase
import com.friends.ggiriggiri.ui.notification.NotificationEntity
import com.friends.ggiriggiri.ui.notification.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class NotificationSaveWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: return Result.failure()
        val message = inputData.getString("message") ?: return Result.failure()
        val timestamp = inputData.getLong("timestamp", System.currentTimeMillis())
        val userDocumentId = inputData.getString("userDocumentId") ?: return Result.failure()

        // Room DB에 저장 (runBlocking 사용하여 즉시 실행)
        runBlocking(Dispatchers.IO) {
            val database = NotificationDatabase.getDatabase(applicationContext)
            val repository = NotificationRepository(database.notificationDao())
            repository.insert(
                NotificationEntity(title = title, message = message, timestamp = timestamp, userDocumentID = userDocumentId)
            )
            Log.d("NotificationSaveWorker","NotificationSaveWorker")
        }

        return Result.success()
    }
}
