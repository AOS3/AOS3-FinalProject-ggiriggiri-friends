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
import com.friends.ggiriggiri.ui.first.register.UserModel
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class RequestFragment : Fragment() {

    private lateinit var binding: FragmentRequestBinding
    private val viewModel: RequestViewModel by viewModels()

    // 사진이 저장될 경로
    private lateinit var filePath: String
    private lateinit var contentUri: Uri
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestBinding.inflate(inflater, container, false)

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
            saveRequestToFirestore { success ->
                if (success) {
                    navigateToHomeFragment() // 🔥 Firestore 저장 완료 후 화면 이동
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

    // Firestore에 요청 저장
    private fun saveRequestToFirestore(onComplete: (Boolean) -> Unit) {
        val loginUser = (requireActivity().application as App).loginUserModel
        val requestMessage = binding.etRequestInput.text.toString()

        if (requestMessage.isNotEmpty()) {
            contentUri?.let { uri ->
                uploadImageToFirebase(uri, { imageUrl ->
                    saveRequest(requestMessage, imageUrl, loginUser, onComplete)
                }, {
                    Toast.makeText(requireContext(), "이미지 업로드 실패!", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                })
            } ?: saveRequest(requestMessage, "", loginUser, onComplete)
        } else {
            Toast.makeText(requireContext(), "메시지를 입력해주세요!", Toast.LENGTH_SHORT).show()
            onComplete(false)
        }
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
                Toast.makeText(requireContext(), "요청 저장 실패!", Toast.LENGTH_SHORT).show()
                onComplete(false)
            }
        )
    }

    // Firebase Storage에 이미지 업로드 후 다운로드 URL 반환
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
}
