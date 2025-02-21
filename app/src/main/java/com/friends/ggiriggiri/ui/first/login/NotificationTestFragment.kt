package com.friends.ggiriggiri.ui.first.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentLoginBinding
import com.friends.ggiriggiri.databinding.FragmentNotificationTestBinding
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class NotificationTestFragment : Fragment() {

    private var _binding: FragmentNotificationTestBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginActivity: LoginActivity

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationTestBinding.inflate(inflater, container, false)
        loginActivity = activity as LoginActivity

        settingButton()

        return binding.root
    }

    //나 자신에게 알림을 보냄
    private fun settingButton() {
        binding.apply {
            button.setOnClickListener {
                Log.d("FCM","버튼눌림")
                FirebaseMessaging.getInstance().token
                    .addOnSuccessListener { token ->
                        if (token != null) {
                            Log.d("FCM", "현재 FCM 토큰: $token")

                            sendPushNotification(
                                token, // 가져온 FCM 토큰 사용
                                "끼리끼리",
                                "Firebase Functions을 통해 전송된 알림 \n ${token}"
                            )

                        } else {
                            Log.e("FCM", "FCM 토큰을 못가져옴.")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FCM", "FCM 토큰 가져오기 실패: ${e.message}")
                    }
            }
        }
    }

    private fun sendPushNotification(targetToken: String, title: String, message: String) {
        if (targetToken.isEmpty()) {
            println("Error: targetToken 값이 비어 있음!")
            return
        }

        val data = JSONObject().apply {
            put("title", title)
            put("body", message)
            put("token", targetToken)
        }

        val requestBody = data.toString()
        val request = Request.Builder()
            .url("https://asia-northeast3-ggiriggiri-c33b2.cloudfunctions.net/sendNotification")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .addHeader("Content-Type", "application/json") // 🔹 JSON 타입 명시
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FCM", "알림 전송 실패: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful) {
                    Log.d("FCM", "알림 전송 성공: $responseBody")
                } else {
                    Log.e("FCM", "알림 전송 실패 (서버 응답 오류): $responseBody")
                }
            }
        })
    }




}