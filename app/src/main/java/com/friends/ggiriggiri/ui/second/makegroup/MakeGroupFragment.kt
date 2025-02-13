package com.friends.ggiriggiri.ui.second.makegroup

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentMakeGroupBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog

class MakeGroupFragment : Fragment() {

    private lateinit var fragmentMakeGroupBinding: FragmentMakeGroupBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentMakeGroupBinding = FragmentMakeGroupBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        checkGroupCode()

        makeGroup()

        return fragmentMakeGroupBinding.root
    }

    // 중복확인 버튼 메서드
    private fun checkGroupCode(){
        fragmentMakeGroupBinding.apply {
            btnMakeGroupCheckCode.setOnClickListener {

                // 일단 예시로 성공
                showCheckCodeDialog()
            }
        }
    }

    // 그룹 만들기 버튼 눌렀을 때
    private fun makeGroup(){
        fragmentMakeGroupBinding.apply {
            btnMakeGroupMakeGroup.setOnClickListener {
                // 유효성 검사

                // 그룹 명이 비어있으면
                if(tfMakeGroupGroupName.editText?.text.toString().isEmpty()){
                    tfMakeGroupGroupName.error = "그룹 명을 입력해주세요."
                    return@setOnClickListener
                } else {
                    tfMakeGroupGroupName.helperText = " "
                }

                // 비밀번호가 6자리 이상인지
                if(tfMakeGroupPassword1.editText?.text.toString().length < 6){
                    tfMakeGroupPassword1.error = "비밀번호는 6자리 이상이어야 합니다."
                    return@setOnClickListener
                } else {
                    tfMakeGroupPassword1.helperText = " "
                }

                // 비밀번호가 일치하는지
                if(tfMakeGroupPassword1.editText?.text.toString() != tfMakeGroupPassword2.editText?.text.toString()){
                    tfMakeGroupPassword2.error = "비밀번호가 일치하지 않습니다."
                    return@setOnClickListener
                } else{
                    tfMakeGroupPassword2.helperText = " "
                }


                // 그룹코드가 enabled = false 가 아닐때
                if(fragmentMakeGroupBinding.btnMakeGroupCheckCode.isEnabled){
                    tfMakeGroupGroupCode.error = "중복확인을 해주세요."
                    return@setOnClickListener
                } else {
                    tfMakeGroupGroupCode.helperText = " "
                }

                // 일단 예시로 성공
                makeGroupSuccessDialog()
            }
        }
    }

    // 중복확인 다이얼로그 표시 메서드
    private fun showCheckCodeDialog() {
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "사용 가능한 그룹 코드입니다.\n 사용하시겠습니까?",
            icon = R.drawable.ic_check_circle, // 아이콘 리소스
            positiveText = "확인",
            onPositiveClick = {
                fragmentMakeGroupBinding.btnMakeGroupCheckCode.isEnabled = false
                fragmentMakeGroupBinding.btnMakeGroupCheckCode.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#B2B1B4"))
                fragmentMakeGroupBinding.btnMakeGroupCheckCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                fragmentMakeGroupBinding.tfMakeGroupGroupCode.isEnabled = false
            },
            negativeText = "취소",
            onNegativeClick = {
                // 취소 버튼 클릭 시 동작
            }
        )
        dialog.showCustomDialog()
    }

    // 그룹이 생성 되었을때 다이얼로그
    private fun makeGroupSuccessDialog(){
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "그룹이 생성되었습니다.",
            icon = R.drawable.ic_check_circle, // 아이콘 리소스
            positiveText = "확인",
            onPositiveClick = {
                // 확인 누를시 작동
            },
        )
        dialog.showCustomDialog()
    }
}