package com.friends.ggiriggiri.ui.third.request

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.databinding.FragmentRequestBinding
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import com.friends.ggiriggiri.ui.first.register.UserModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class RequestFragment : Fragment() {

    private lateinit var binding: FragmentRequestBinding
    private val viewModel: RequestViewModel by viewModels()
    private lateinit var progressDialog: CustomDialogProgressbar

    // 사진이 저장될 경로
    private lateinit var filePath: String
    private lateinit var contentUri: Uri
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestBinding.inflate(inflater, container, false)
        progressDialog = CustomDialogProgressbar(requireContext())

        filePath = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()

        setupObservers()
        setupListeners()
        createCameraLauncher()

        return binding.root
    }

    // ViewModel 관찰 및 UI 업데이트
    private fun setupObservers() {
        viewModel.isImageUploaded.observe(viewLifecycleOwner) { isUploaded ->
            if (isUploaded) {
                binding.btnRequestUploadImage.visibility = View.GONE
            }
        }

        viewModel.isSubmitEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnRequestSubmit.isEnabled = isEnabled
        }
    }

    // UI 이벤트 설정
    private fun setupListeners() {
        binding.tbRequest.setNavigationOnClickListener {
            navigateToHomeFragment()
        }

        binding.btnRequestUploadImage.setOnClickListener {
            openCamera()
        }

        binding.etRequestInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setTextEntered(!s.isNullOrEmpty())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnRequestSubmit.setOnClickListener {
            binding.btnRequestSubmit.isEnabled = false
            progressDialog.show()
            saveRequestToFirestore { success ->
                progressDialog.dismiss()
                if (success) {
                    navigateToHomeFragment()
                } else {
                    binding.btnRequestSubmit.isEnabled = true
                }
            }
        }

        binding.etRequestInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentLength = s?.length ?: 0

                // 현재 입력된 글자 수 / 최대 글자 수 업데이트
                binding.tvRequestCharCount.text = "$currentLength/100"

                // 텍스트 입력 상태 업데이트
                viewModel.setTextEntered(currentLength > 0)

                // 글자 수가 100자를 초과하면 경고 메시지 표시 및 입력 제한
                if (currentLength >= 100) {
                    Toast.makeText(requireContext(), "최대 100자까지 입력할 수 있습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // 최대 글자 수(100자) 초과 시 추가 입력 방지
                if (s != null && s.length > 100) {
                    s.delete(100, s.length)
                }
            }
        })
    }

    // Firestore에 요청 저장 후 그룹원들에게 알림 전송
    private fun saveRequestToFirestore(onComplete: (Boolean) -> Unit) {
        val loginUser = (requireActivity().application as App).loginUserModel
        val requestMessage = binding.etRequestInput.text.toString()

        // 이미지와 메시지가 필수이므로 예외처리 필요 없음
        uploadImageToFirebase(contentUri, { imageUrl ->
            saveRequest(requestMessage, imageUrl, loginUser) { success ->
                if (success) {
                    sendGroupPushNotification(loginUser.userGroupDocumentID, requestMessage)
                }
                onComplete(success)
            }
        }, {
            progressDialog.dismiss()
            binding.btnRequestSubmit.isEnabled = true
            Toast.makeText(requireContext(), "이미지 업로드 실패!", Toast.LENGTH_SHORT).show()
            onComplete(false)
        })
    }

    // Firestore에 저장 함수 (이미지 URL 포함)
    private fun saveRequest(requestMessage: String, imageUrl: String, loginUser: UserModel, onComplete: (Boolean) -> Unit) {
        viewModel.saveRequest(
            userDocumentId = loginUser.userDocumentId,
            requestMessage = requestMessage,
            requestImage = imageUrl,
            groupDocumentId = loginUser.userGroupDocumentID,
            onSuccess = { requestId ->
                Toast.makeText(requireContext(), "요청이 저장되었습니다! ID: $requestId", Toast.LENGTH_SHORT).show()
                onComplete(true)
            },
            onFailure = {
                progressDialog.dismiss()
                binding.btnRequestSubmit.isEnabled = true
                Toast.makeText(requireContext(), "요청 저장 실패!", Toast.LENGTH_SHORT).show()
                onComplete(false)
            }
        )
    }

    private fun uploadImageToFirebase(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val storageRef = Firebase.storage.reference.child("request_images/${System.currentTimeMillis()}.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }.addOnFailureListener {
                    onFailure()
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    // 카메라 실행
    private fun openCamera() {
        val fileName = "temp_${System.currentTimeMillis()}.jpg"
        val picPath = "$filePath/$fileName"
        val file = File(picPath)

        contentUri = FileProvider.getUriForFile(
            requireContext(),
            "com.friends.ggiriggiri.fileprovider",
            file
        )

        cameraLauncher.launch(contentUri)
    }

    // 카메라 런처 설정
    private fun createCameraLauncher() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                processCapturedImage()
            } else {
                Toast.makeText(requireContext(), "사진 촬영을 취소했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 촬영된 이미지 처리
    private fun processCapturedImage() {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(contentUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            if (bitmap != null) {
                val rotatedBitmap = rotateBitmap(bitmap, getDegree(contentUri))

                binding.ivRequestUploadedImage.apply {
                    visibility = View.VISIBLE
                    setImageBitmap(rotatedBitmap)
                }

                binding.ivRequestImagePlaceholder.visibility = View.GONE
                binding.tvRequestImageGuide.visibility = View.GONE
                binding.tvRequestImageSubGuide.visibility = View.GONE
                binding.btnRequestUploadImage.visibility = View.GONE

                viewModel.setImageUploaded(true)
            } else {
                Toast.makeText(requireContext(), "이미지를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "사진을 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 이미지 회전 보정
    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix().apply { postRotate(degree.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
    }

    // 사진의 회전 각도 가져오기
    private fun getDegree(uri: Uri): Int {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            inputStream?.use {
                val exifInterface = ExifInterface(it)
                when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            } ?: 0
        } catch (e: IOException) {
            e.printStackTrace()
            0
        }
    }

    // HomeFragment로 이동
    private fun navigateToHomeFragment() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun sendGroupPushNotification(groupDocumentId: String, message: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("GroupData").document(groupDocumentId).get()
            .addOnSuccessListener { document ->
                val userDocumentIds = document.get("groupUserDocumentID") as? List<String>
                if (userDocumentIds.isNullOrEmpty()) return@addOnSuccessListener

                val batchSize = 20
                userDocumentIds.chunked(batchSize).forEach { batchIds ->
                    val userFetchTasks = batchIds.map { userId ->
                        db.collection("UserData").document(userId).get()
                    }

                    Tasks.whenAllSuccess<DocumentSnapshot>(userFetchTasks)
                        .addOnSuccessListener { results ->
                            val fcmTokens = results.filter { it.exists() }
                                .flatMap { userDoc ->
                                    val fcmCodeList = userDoc.get("userFcmCode")
                                    when (fcmCodeList) {
                                        is List<*> -> fcmCodeList.filterIsInstance<String>()
                                        is String -> listOf(fcmCodeList)
                                        else -> emptyList()
                                    }
                                }

                            if (fcmTokens.isNotEmpty()) {
                                sendPushNotification(fcmTokens, "새로운 요청이 도착했습니다!", message)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FCM", "FCM 토큰 조회 실패: ${e.message}")
                        }
                }
            }
    }

    private fun sendPushNotification(targetTokens: List<String>, title: String, message: String) {
        if (targetTokens.isEmpty()) {
            Log.e("FCM", "알림 전송 실패: targetTokens가 비어 있음!")
            return
        }

        val client = OkHttpClient()

        // 중복 제거 후 전송
        val uniqueTokens = targetTokens.distinct()

        uniqueTokens.forEach { token ->
            val data = JSONObject().apply {
                put("title", title)
                put("body", message)
                put("token", token)
            }

            val requestBody = data.toString()
            Log.d("FCM", "FCM 전송 데이터: $requestBody") // 실제 전송 데이터 확인

            val request = Request.Builder()
                .url("https://asia-northeast3-ggiriggiri-c33b2.cloudfunctions.net/sendNotification")
                .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("FCM", "알림 전송 실패 (네트워크 문제): ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    Log.d("FCM", "서버 응답 코드: ${response.code}")
                    Log.d("FCM", "서버 응답 내용: $responseBody")

                    if (response.isSuccessful) {
                        Log.d("FCM", "알림 전송 성공!")
                    } else {
                        Log.e("FCM", "알림 전송 실패 (서버 오류): HTTP ${response.code} - $responseBody")

                        // 무효한 토큰이면 Firestore에서 삭제
                        if (responseBody?.contains("Requested entity was not found.") == true) {
                            Log.e("FCM", "유효하지 않은 토큰 발견: $token")
                            removeInvalidToken(token)
                        }
                    }
                }
            })
        }
    }

    private fun removeInvalidToken(token: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("UserData")
            .whereArrayContains("userFcmCode", token)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.id
                    val existingTokens = document.get("userFcmCode") as? List<String> ?: emptyList()
                    val updatedTokens = existingTokens.filter { it != token }

                    db.collection("UserData").document(userId)
                        .update("userFcmCode", updatedTokens)
                        .addOnSuccessListener {
                            Log.d("FCM", "무효한 토큰 제거 완료: $token")

                            // Firestore 데이터가 반영되었는지 확인
                            db.collection("UserData").document(userId)
                                .get()
                                .addOnSuccessListener { updatedDoc ->
                                    val refreshedTokens = updatedDoc.get("userFcmCode") as? List<String> ?: emptyList()
                                    if (!refreshedTokens.contains(token)) {
                                        Log.d("FCM", "Firestore에서 무효한 토큰 최종 제거 확인 완료: $token")
                                    } else {
                                        Log.e("FCM", "Firestore에서 무효한 토큰 제거 실패: $token")
                                    }
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FCM", "무효한 토큰 제거 실패: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FCM", "무효한 토큰 조회 실패: ${e.message}")
            }
    }
}
