package com.friends.ggiriggiri.data.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.ui.notification.NotificationDatabase
import com.friends.ggiriggiri.ui.notification.NotificationEntity
import com.friends.ggiriggiri.ui.notification.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationSaveService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent?.getStringExtra("title") ?: return START_NOT_STICKY
        val message = intent.getStringExtra("message") ?: return START_NOT_STICKY
        val timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis())
        val userDocumentId = intent.getStringExtra("userDocumentId") ?: return START_NOT_STICKY

        startForegroundService()

        CoroutineScope(Dispatchers.IO).launch {
            val database = NotificationDatabase.getDatabase(applicationContext)
            val repository = NotificationRepository(database.notificationDao())

            val notificationEntity = NotificationEntity(
                title = title,
                message = message,
                timestamp = timestamp,
                userDocumentID = userDocumentId
            )

            repository.insert(notificationEntity)

            stopSelf() // 데이터 저장 후 서비스 종료
        }

        return START_NOT_STICKY
    }

    private fun startForegroundService() {
        val channelId = "notification_service_channel"
        val channelName = "Notification Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("알림 저장 중")
            .setContentText("알림을 저장하는 중입니다...")
            .setSmallIcon(R.drawable.main_logo_background)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
