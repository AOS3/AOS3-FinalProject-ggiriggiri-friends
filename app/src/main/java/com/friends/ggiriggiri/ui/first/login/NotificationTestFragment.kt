package com.friends.ggiriggiri.ui.first.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentNotificationTestBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
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
    ): View {
        _binding = FragmentNotificationTestBinding.inflate(inflater, container, false)
        loginActivity = activity as LoginActivity


        settingButton()
        settingButton2() // buttonGroupTest용 설정

        Log.d("시간",System.currentTimeMillis().toString())

        return binding.root
    }

    // 기존 버튼: 나 자신에게 알림을 보내는 예제 (FCM 토큰 활용)
    private fun settingButton() {
        binding.apply {
            button.setOnClickListener {
                Log.d("FCM", "버튼 눌림")
                FirebaseMessaging.getInstance().token
                    .addOnSuccessListener { token ->
                        if (token != null) {
                            Log.d("FCM", "현재 FCM 토큰: $token")

                            sendPushNotification(
                                "fTMS_AzXRdqGcoyPYyjF2b:APA91bHbPvIElXTWGXIAbktVTZDsWXYnBsU3hJLuegjazYdRthgifle_axUzSAnvbg7ogPRjwQ4u4PX-AqE85v3_5Dpy_Tv2d_yDnETJryLZ7S-KZtyS4xo", // 가져온 FCM 토큰 사용
                                "끼리끼리",
                                "Firebase Functions을 통해 전송된 알림 \n $token"
                            )

                        } else {
                            Log.e("FCM", "FCM 토큰을 가져오지 못함.")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FCM", "FCM 토큰 가져오기 실패: ${e.message}")
                    }
            }
        }
    }

    // OkHttp를 사용하여 별도의 클라우드 펑션 (sendNotification) 호출하는 예제
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
            .addHeader("Content-Type", "application/json")
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

    // 두 번째 버튼: Firebase Functions의 callable 함수를 호출하여 그룹 관련 알림 전송
//    private fun settingButton2() {
//        binding.apply {
//            buttonGroupTest.setOnClickListener {
//                Log.d("Functions", "buttonGroupTest 버튼 눌림")
//                callTriggerNotification()
//            }
//        }
//    }

    private fun settingButton2() {
        binding.apply {
            buttonGroupTest.setOnClickListener {
                runEveryDayQuestionManually()
            }
        }
    }
    private fun runEveryDayQuestionManually() {
        lateinit var functions: FirebaseFunctions

        functions = FirebaseFunctions.getInstance("asia-northeast3") // Firebase Functions 지역 설정
        functions
            .getHttpsCallable("runEveryDayQuestionManually")
            .call()
            .addOnSuccessListener { result ->
                val data = result.getData() // getData() 직접 호출
                Log.d("FirebaseFunctions", "성공: $data")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseFunctions", "오류 발생", e)
            }
    }




    // Firebase Functions의 callable 함수 "triggerQuestionNotification" 호출
    private fun callTriggerNotification() {
        FirebaseFunctions.getInstance("asia-northeast3")
            .getHttpsCallable("triggerQuestionNotification")
            .call()
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    Log.e("Functions", "알림 전송 호출 중 에러 발생", e)
                    Toast.makeText(context, "알림 전송 실패", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
                Toast.makeText(context, "알림 전송 호출 성공!", Toast.LENGTH_SHORT).show()
                Log.d("Functions", "알림 전송 호출 성공")
            }
    }
}
