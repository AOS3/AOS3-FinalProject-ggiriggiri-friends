package com.friends.ggiriggiri.ui.fourth.mypage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.friends.ggiriggiri.R
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
                    // 선택한 이미지를 프로필 이미지로 설정
                }
            }
        }

        profileModifyClick()

        return fragmentMyPageBottomSheetBinding.root
    }

    // 프로필 사진 보기
    private fun profileViewClick() {
        fragmentMyPageBottomSheetBinding.apply {
            profileView.setOnClickListener {
                // 다이얼 로그로 이동
            }
        }
    }

    // 프로필 사진 변경
    private fun profileModifyClick(){
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