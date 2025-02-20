package com.friends.ggiriggiri.ui.fourth.settinggroup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentSettingGroupBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.fourth.modifygroupname.ModifyGroupNameFragment
import com.friends.ggiriggiri.ui.fourth.modifygrouppw.ModifyGroupPwFragment
import com.friends.ggiriggiri.ui.fourth.modifyuserpw.ModifyUserPwFragment
import com.friends.ggiriggiri.ui.fourth.mypage.MyPageFragment
import com.google.android.material.transition.MaterialSharedAxis

class SettingGroupFragment : Fragment() {

    lateinit var fragmentSettingGroupBinding: FragmentSettingGroupBinding
    lateinit var socialActivity: SocialActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentSettingGroupBinding = FragmentSettingGroupBinding.inflate(inflater)
        socialActivity = activity as SocialActivity

        settingToolbar()

        setupGroupOptions()

        return fragmentSettingGroupBinding.root
    }

    // Toolbar
    private fun settingToolbar(){
        fragmentSettingGroupBinding.apply {
            toolbarGroupSetting.setTitle("그룹 설정")
            toolbarGroupSetting.setNavigationIcon(R.drawable.ic_arrow_back_ios)
            toolbarGroupSetting.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }


    // 각 텍스트 동작 함수
    private fun setupGroupOptions() {
        fragmentSettingGroupBinding.apply {
            modifyGroupPw.text = "그룹 비밀번호 변경"
            modifyGroupName.text = "그룹명 변경"
            modifyGroupExit.text = "그룹 나가기"
            val groupItemClickListener = View.OnClickListener { view ->
                when (view.id) {
                    R.id.modifyGroupPw -> {
                        // 그룹 비밀번호 변경 동작 구현
                        socialActivity.replaceFragment(ModifyGroupPwFragment())
                    }

                    R.id.modifyGroupName -> {
                        // 그룹명 변경 동작 구현
                        socialActivity.replaceFragment(ModifyGroupNameFragment())
                    }

                    R.id.modifyGroupExit -> {
                        modifyGroupExitDialog()
                    }
                }
            }
            modifyGroupPw.setOnClickListener(groupItemClickListener)
            modifyGroupName.setOnClickListener(groupItemClickListener)
            modifyGroupExit.setOnClickListener(groupItemClickListener)
        }
    }

    // 그룹 나가기 다이얼로그
    private fun modifyGroupExitDialog(){
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "정말 그룹을 나가시겠습니까?",
            icon = R.drawable.ic_group_off,
            positiveText = "예",
            onPositiveClick = {

            },
            negativeText = "아니오",
            onNegativeClick = {

            }
        )
        dialog.showCustomDialog()
    }
}