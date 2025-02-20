package com.friends.ggiriggiri.ui.fourth.mypage

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

        // ActivityResultLauncher 초기화
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    // 부모 Fragment에 선택한 이미지 전달
                    val parentFragment = parentFragment as? MyPageFragment
                    parentFragment?.updateProfileImage(imageUri)
                    dismiss() // BottomSheet 닫기
                }
            }
        }

        profileViewClick()
        profileModifyClick()

        return fragmentMyPageBottomSheetBinding.root
    }

    // 프로필 사진 보기
    private fun profileViewClick() {
        fragmentMyPageBottomSheetBinding.apply {
            profileView.setOnClickListener {
                val parentFragment = parentFragment as? MyPageFragment
                val imageUri = parentFragment?.getProfileImageUri() // 부모 Fragment에서 URI 가져오기

                if (imageUri != null) {
                    // FullScreenImageDialogFragment 호출
                    val dialog = FullScreenImageDialogFragment.newInstance(imageUri)
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
                }
                // ActivityResultLauncher로 Intent 실행
                pickImageLauncher.launch(intent)
            }
        }
    }
}