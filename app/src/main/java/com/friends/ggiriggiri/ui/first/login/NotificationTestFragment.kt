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
                                "테스트 알림",
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

        val data = hashMapOf(
            "title" to title,
            "body" to message,
            "token" to targetToken
        )

        FirebaseFunctions.getInstance()
            .getHttpsCallable("sendNotification")
            .call(data)
            .addOnSuccessListener { result ->
                val responseData = result.getData() as Map<*, *>
                println("알림 전송 성공: ${responseData["message"]}")
            }
            .addOnFailureListener { e ->
                println("알림 전송 실패: ${e.message}")
            }
    }


}