package com.friends.ggiriggiri.ui.mypages.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.friends.ggiriggiri.databinding.FragmentMyPageBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyPageBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var fragmentMyPageBottomSheetBinding: FragmentMyPageBottomSheetBinding

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        fragmentMyPageBottomSheetBinding = FragmentMyPageBottomSheetBinding.inflate(layoutInflater)

        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    // 영구적 퍼미션 요청 (persistable permission)
                    val takeFlags = result.data?.flags?.and(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    ) ?: 0
                    try {
                        requireContext().contentResolver.takePersistableUriPermission(imageUri, takeFlags)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    // 부모 Fragment에 선택한 이미지 전달
                    (parentFragment as? MyPageFragment)?.updateProfileImage(imageUri)
                    dismiss()
                }
            }
        }

        profileViewClick()
        profileModifyClick()

        return fragmentMyPageBottomSheetBinding.root
    }


    // 프로필 사진 보기 버튼 클릭 시 안전한 URI를 이용해 전체화면 다이얼로그 실행
    private fun profileViewClick() {
        fragmentMyPageBottomSheetBinding.apply {
            profileView.setOnClickListener {
                val parentFragment = parentFragment as? MyPageFragment
                // 부모 Fragment에서 안전한 프로필 이미지 URI 얻기
                val secureUri = parentFragment?.getSecureProfileImageUri()
                if (secureUri != null) {
                    // FullScreenImageDialogFragment 호출 (안전한 URI 전달)
                    val dialog = FullScreenImageDialogFragment.newInstance(secureUri)
                    dialog.show(parentFragmentManager, "FullScreenImageDialog")
                } else {
                    Toast.makeText(context, "프로필 이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 프로필 사진 변경
    private fun profileModifyClick() {
        fragmentMyPageBottomSheetBinding.apply {
            profileModify.setOnClickListener {
                // 앨범 연결
                // 갤러리 Intent 생성
                val intent = Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                // ActivityResultLauncher로 Intent 실행
                pickImageLauncher.launch(intent)
            }
        }
    }
}