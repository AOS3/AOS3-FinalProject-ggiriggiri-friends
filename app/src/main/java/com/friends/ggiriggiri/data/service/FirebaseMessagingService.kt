package com.friends.ggiriggiri.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.ui.notification.NotificationDatabase
import com.friends.ggiriggiri.ui.notification.NotificationEntity
import com.friends.ggiriggiri.ui.notification.NotificationRepository
import com.friends.ggiriggiri.ui.notification.UserPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirebaseNotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "알림"
        val message = remoteMessage.notification?.body ?: "새로운 메시지가 도착했습니다."
        val timestamp = System.currentTimeMillis()
        val userDocumentId = UserPreferences.getUserID(applicationContext) ?: return

        val intent = Intent(this, NotificationSaveService::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("timestamp", timestamp)
            putExtra("userDocumentId", userDocumentId)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        sendNotification(title, message)
    }

    private fun saveNotificationToDB(title: String, message: String, timestamp: Long, userDocumentId: String) {
        val notification = NotificationEntity(
            title = title,
            message = message,
            timestamp = timestamp,
            userDocumentID = userDocumentId
        )

        CoroutineScope(Dispatchers.IO).launch {
            val database = NotificationDatabase.getDatabase(applicationContext)
            val repository = NotificationRepository(database.notificationDao())
            repository.insert(notification)
        }
    }

    private fun sendNotification(title: String, messageBody: String) {
        val channelId = "fcm_default_channel"

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.main_logo_background)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FCM 알림",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}
