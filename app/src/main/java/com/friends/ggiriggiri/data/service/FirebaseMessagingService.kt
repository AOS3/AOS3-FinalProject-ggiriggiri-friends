package com.friends.ggiriggiri.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 메시지가 정상적으로 수신되었는지 확인
        Log.d("FCM", "알림 수신: ${remoteMessage.notification?.title} - ${remoteMessage.notification?.body}")

        // 알림 데이터 처리
        remoteMessage.notification?.let {
            sendNotification(it.title ?: "알림", it.body ?: "새로운 메시지가 도착했습니다.")
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "새로운 FCM 토큰: $token")

        // Firestore에 새로운 FCM 토큰 업데이트 (사용자의 ID 필요)
        val userId = "사용자_ID_여기에_넣기" // 여기에 현재 로그인한 사용자 ID를 가져와야 함
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .update("fcmToken", token)
            .addOnSuccessListener { Log.d("FCM", "토큰 업데이트 성공") }
            .addOnFailureListener { e -> Log.e("FCM", "토큰 업데이트 실패: ${e.message}") }
    }

    private fun sendNotification(title: String, messageBody: String) {
        val channelId = "fcm_default_channel"

        // 알림 클릭 시 LoginActivity 실행
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

        // Android 8.0 이상에서 알림 채널 필요함
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