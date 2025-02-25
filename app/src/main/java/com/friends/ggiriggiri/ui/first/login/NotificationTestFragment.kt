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
                                token, // 가져온 FCM 토큰 사용
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
    private fun settingButton2() {
        binding.apply {
            buttonGroupTest.setOnClickListener {
                Log.d("Functions", "buttonGroupTest 버튼 눌림")
                callTriggerNotification()
            }
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

    fun uploadQuestionsToFirestore() {
        val db = FirebaseFirestore.getInstance()
        val questionList = listOf(
            "나를 가장 화나게 한 순간은?" to "#FF3B30",
            "둘이 먹다 하나 죽어도 모를 나의 최애 음식은?" to "#FF9500",
            "만약 우리 중 한 명이 연예인이 된다면, 누가 어떤 이유로 뜰까?" to "#FFD60A",
            "연락을 가장 안 받는 사람은?" to "#808080",
            "머리에 돌만 들어있을 것 같은 사람은?" to "#A6A6A6",
            "가장 눈치 없는 사람은?" to "#FF5E57",
            "우리 중 갑자기 대기업 CEO가 될 것 같은 사람은?" to "#1D4ED8",
            "가장 감정 기복이 심한 사람은?" to "#9B51E0",
            "술을 가장 못 마실 것 같은 사람은?" to "#FF4F4F",
            "여행 가면 짐을 가장 많이 챙길 것 같은 사람은?" to "#FACC15",
            "우리 중 결혼을 제일 먼저 할 것 같은 사람은?" to "#FA8072",
            "반대로 가장 늦게 결혼할 것 같은 사람은?" to "#4B5563",
            "우리 중 갑자기 외국으로 이민 갈 것 같은 사람은?" to "#3498DB",
            "혼자 있어도 절대 심심하지 않을 것 같은 사람은?" to "#10B981",
            "연애할 때 가장 헌신적일 것 같은 사람은?" to "#E91E63",
            "친구 중에서 가장 현실적인 사람은?" to "#374151",
            "친구들 중 가장 독특한 취향을 가진 사람은?" to "#F472B6",
            "제일 먼저 백만장자가 될 것 같은 사람은?" to "#F4A261",
            "길거리 캐스팅 당할 확률이 가장 높은 사람은?" to "#EAB308",
            "평생 집순이(집돌이)로 살 것 같은 사람은?" to "#6B7280",
            "어떤 상황에서도 멘탈이 안 흔들릴 것 같은 사람은?" to "#2563EB",
            "친구들 사이에서 가장 조용한 사람은?" to "#64748B",
            "반대로 가장 수다스러운 사람은?" to "#F59E0B",
            "가장 자기애가 강한 사람은?" to "#DC2626",
            "기분이 태도가 되는 사람은?" to "#9CA3AF",
            "세상 고민 다 짊어진 표정을 자주 짓는 사람은?" to "#374151",
            "제일 애교가 많은 사람은?" to "#FFC0CB",
            "우리 중 가장 깔끔한 사람은?" to "#ECF0F1",
            "돈 관리를 제일 못할 것 같은 사람은?" to "#E63946",
            "우리 중 갑자기 사라질 것 같은 사람은?" to "#8D99AE",
            "남들이 잘 모르는 나의 이상한 습관은?" to "#A78BFA",
            "내가 한 가장 어이없는 소비는?" to "#FF8C00",
            "친구들 중 갑자기 유튜브를 시작할 것 같은 사람은?" to "#C13584",
            "나의 최악의 흑역사는?" to "#121212",
            "나만의 이상한 징크스가 있다면?" to "#BB86FC",
            "가장 고집이 센 사람은?" to "#B91C1C",
            "하루 종일 누워있어도 안 질릴 것 같은 사람은?" to "#48CAE4",
            "우리 중에서 가장 변덕이 심한 사람은?" to "#FBBF24",
            "친구 중 가장 감성적인 사람은?" to "#A855F7",
            "친구들 중 가장 철이 덜 든 사람은?" to "#F97316",
            "가장 유머 감각이 좋은 사람은?" to "#22C55E",
            "여행 가면 길 잃을 것 같은 사람은?" to "#8B5CF6",
            "우리 중 갑자기 사법고시 붙을 것 같은 사람은?" to "#0D9488",
            "갑자기 뮤지컬 배우가 될 것 같은 사람은?" to "#D97706",
            "가장 돌발 행동이 많은 사람은?" to "#FB7185",
            "나에게 있어 가장 소중한 물건은?" to "#FFDD57",
            "갑자기 사기꾼한테 당할 것 같은 사람은?" to "#DC2626",
            "나를 가장 잘 아는 사람은?" to "#2563EB",
            "우리 중에서 가장 선물을 고르는 센스가 좋은 사람은?" to "#34D399",
            "만약 다시 태어난다면 나는 어떤 모습으로 태어나고 싶을까?" to "#F87171"
        )

        questionList.forEachIndexed { index, pair ->
            val questionData = hashMapOf(
                "color" to pair.second,
                "content" to pair.first,
                "imgUrl" to "",
                "number" to index + 1
            )

            db.collection("QuestionList")
                .document((index + 1).toString())  // 문서 이름을 1~50 숫자로 설정
                .set(questionData)
                .addOnSuccessListener {
                    println("질문 ${index + 1} 업로드 성공!")
                }
                .addOnFailureListener { e ->
                    println("질문 ${index + 1} 업로드 실패: ${e.message}")
                }
        }
    }
}
