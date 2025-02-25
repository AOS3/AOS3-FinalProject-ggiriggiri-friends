package com.friends.ggiriggiri.ui.third.response

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
import com.friends.ggiriggiri.data.model.ResponseModel
import com.friends.ggiriggiri.databinding.FragmentResponseBinding
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class ResponseFragment : Fragment() {

    private lateinit var binding: FragmentResponseBinding
    private val viewModel: ResponseViewModel by viewModels()

    private var requestUserDocumentId: String? = null
    private var requestMessage: String? = null
    private var requestId: String? = null
    private lateinit var progressDialog: CustomDialogProgressbar

    // 사진이 저장될 경로
    private lateinit var filePath: String
    // 저장된 파일에 접근하기 위한 Uri
    private lateinit var contentUri: Uri
    // 카메라 런처
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResponseBinding.inflate(inflater, container, false)
        progressDialog = CustomDialogProgressbar(requireContext())

        // 파일 저장 경로 설정
        filePath = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()

        requestUserDocumentId = arguments?.getString("requestUserDocumentId")
        requestMessage = arguments?.getString("requestMessage")
        requestId = arguments?.getString("requestId")

        if (requestId.isNullOrEmpty()) {
            Log.d("ResponseFragment", "요청 ID가 없음")
            Toast.makeText(requireContext(), "오류: 요청 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            navigateToHomeFragment()
            return binding.root
        }

        setupObservers()
        setupListeners()
        createCameraLauncher()

        // 요청한 사용자 정보 가져오기
        requestUserDocumentId?.let { viewModel.fetchRequestUserInfo(it) }

        // 요청 메시지 UI 적용
        binding.tvRespondRequestContent.text = requestMessage ?: "내용 없음"

        return binding.root
    }

    // ViewModel 관찰 및 UI 업데이트
    private fun setupObservers() {
        viewModel.isImageUploaded.observe(viewLifecycleOwner) { isUploaded ->
            if (isUploaded) {
                binding.btnRespondUploadImage.visibility = View.GONE
            }
        }

        viewModel.isSubmitEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnRespondSubmit.isEnabled = isEnabled
        }

        viewModel.requestUser.observe(viewLifecycleOwner) { userName ->
            binding.tvRespondRequester.text = "$userName 님의 요청"
        }
    }

    // UI 이벤트 설정
    private fun setupListeners() {
        binding.tbResponse.setNavigationOnClickListener {
            navigateToHomeFragment()
        }

        binding.btnRespondUploadImage.setOnClickListener {
            openCamera()
        }

        binding.etRespondInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentLength = s?.length ?: 0

                // 현재 입력된 글자 수 / 최대 글자 수 업데이트
                binding.tvRespondCharCount.text = "$currentLength/100"

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

        binding.btnRespondSubmit.setOnClickListener {
            val responseText = binding.etRespondInput.text.toString()
            if (requestId != null && requestUserDocumentId != null) {
                binding.btnRespondSubmit.isEnabled = false
                progressDialog.show()
                saveResponse(responseText)
            } else {
                Toast.makeText(requireContext(), "요청 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveResponse(responseMessage: String) {
        val loginUser = (requireActivity().application as App).loginUserModel

        if (responseMessage.isNotEmpty()) {
            contentUri?.let { uri ->
                uploadImageToFirebase(uri) { imageUrl ->
                    viewModel.submitResponse(requestId!!, loginUser.userDocumentId, responseMessage, imageUrl)
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "응답이 저장되었습니다!", Toast.LENGTH_SHORT).show()
                    navigateToHomeFragment()
                }
            }
        } else {
            progressDialog.dismiss()
            binding.btnRespondSubmit.isEnabled = true
            Toast.makeText(requireContext(), "응답 내용을 입력해주세요!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri, onSuccess: (String) -> Unit) {
        val storageRef = Firebase.storage.reference.child("response_images/${System.currentTimeMillis()}.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    binding.btnRespondSubmit.isEnabled = true
                    Toast.makeText(requireContext(), "이미지 URL 가져오기 실패!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                binding.btnRespondSubmit.isEnabled = true
                Toast.makeText(requireContext(), "이미지 업로드 실패!", Toast.LENGTH_SHORT).show()
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

                binding.ivRespondUploadedImage.apply {
                    visibility = View.VISIBLE
                    setImageBitmap(rotatedBitmap)
                }

                binding.ivRespondImagePlaceholder.visibility = View.GONE
                binding.tvRespondImageGuide.visibility = View.GONE
                binding.tvRespondImageSubGuide.visibility = View.GONE
                binding.btnRespondUploadImage.visibility = View.GONE

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
