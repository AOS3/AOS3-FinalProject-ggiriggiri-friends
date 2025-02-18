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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.databinding.FragmentResponseBinding
import java.io.File
import java.io.IOException

class ResponseFragment : Fragment() {

    private lateinit var binding: FragmentResponseBinding
    private val viewModel: ResponseViewModel by viewModels()

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

        // 파일 저장 경로 설정
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
                binding.btnRespondUploadImage.visibility = View.GONE
            }
        }

        viewModel.isSubmitEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnRespondSubmit.isEnabled = isEnabled
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
                viewModel.setTextEntered(!s.isNullOrEmpty())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnRespondSubmit.setOnClickListener {
            navigateToHomeFragment()
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
