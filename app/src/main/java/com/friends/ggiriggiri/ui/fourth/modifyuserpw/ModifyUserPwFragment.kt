package com.friends.ggiriggiri.ui.fourth.modifyuserpw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentModifyUserPwBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog

class ModifyUserPwFragment : Fragment() {

    lateinit var fragmentModifyUserPwBinding: FragmentModifyUserPwBinding
    lateinit var socialActivity: SocialActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentModifyUserPwBinding = FragmentModifyUserPwBinding.inflate(inflater)
        socialActivity = activity as SocialActivity

        settingToolbar()
        applyButton()

        return fragmentModifyUserPwBinding.root
    }

    // Toolbar
    private fun settingToolbar(){
        fragmentModifyUserPwBinding.apply {
            toolbarModifyGroupPw.setTitle("비밀번호 변경")
            toolbarModifyGroupPw.setNavigationIcon(R.drawable.ic_arrow_back_ios)
            toolbarModifyGroupPw.setNavigationOnClickListener {
                socialActivity.supportFragmentManager.popBackStack()
            }
        }
    }

    // button
    private fun applyButton(){
        fragmentModifyUserPwBinding.apply {
            modifyUserPwButton.setOnClickListener {
                modifyUserPwDialog()
            }
        }
    }

    // 유저 비밀번호 변경 다이얼 로그
    private fun modifyUserPwDialog(){
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "비밀번호를 변경하시겠습니까?",
            icon = R.drawable.ic_logout,
            positiveText = "예",
            onPositiveClick = {
            Toast.makeText(requireContext(), "비밀번호가 변경 되었습니다.", Toast.LENGTH_SHORT).show()
            },
            negativeText = "아니오",
            onNegativeClick = {

            }
        )
        dialog.showCustomDialog()
    }
}