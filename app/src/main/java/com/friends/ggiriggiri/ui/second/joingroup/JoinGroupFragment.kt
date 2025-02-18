package com.friends.ggiriggiri.ui.second.joingroup

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentJoinGroupBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog

class JoinGroupFragment : Fragment() {

    lateinit var fragmentJoinGroupBinding: FragmentJoinGroupBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentJoinGroupBinding = FragmentJoinGroupBinding.inflate(inflater, container, false)

        enterGroup()
        // Inflate the layout for this fragment
        return fragmentJoinGroupBinding.root
    }

    private fun enterGroup(){
        fragmentJoinGroupBinding.apply {
            btnJoinGroupJoin.setOnClickListener {
                // 유효성 검사

                // 그룹 코드가 비어있으면
                if(tfJoinGroupCode.editText?.text.toString().isEmpty()){
                    tfJoinGroupCode.error = "그룹 코드를 입력해주세요."
                    return@setOnClickListener
                } else {
                    tfJoinGroupCode.helperText = " "
                }

                // 비밀번호가 비어있으면
                if(tfJoinGroupPassword.editText?.text.toString().isEmpty()){
                    tfJoinGroupPassword.error = "비밀번호를 입력해주세요."
                    return@setOnClickListener
                } else {
                    tfJoinGroupPassword.helperText = " "
                }

                // 일단 들어가게 하기
                val intent = Intent(requireContext(), SocialActivity::class.java)
                startActivity(intent)

                //noInfoGroupDialog()
            }
        }
    }

    // 해당 정보를 가진 그룹이 없는 다이얼로그
    private fun noInfoGroupDialog(){
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "해당 정보를 가진 그룹이 없습니다.",
            icon = R.drawable.ic_error, // 아이콘 리소스
            positiveText = "확인",
            onPositiveClick = {
                // 확인 누를시 작동
            },
        )
        dialog.showCustomDialog()
    }


}