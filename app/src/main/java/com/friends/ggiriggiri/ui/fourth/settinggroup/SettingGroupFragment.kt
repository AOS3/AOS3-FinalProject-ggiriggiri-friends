package com.friends.ggiriggiri.ui.fourth.settinggroup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.GroupActivity
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentSettingGroupBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.fourth.modifygroupname.ModifyGroupNameFragment
import com.friends.ggiriggiri.ui.fourth.modifygrouppw.ModifyGroupPwFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingGroupFragment : Fragment() {

    lateinit var fragmentSettingGroupBinding: FragmentSettingGroupBinding
    lateinit var socialActivity: SocialActivity

    private val viewModel: SettingGroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentSettingGroupBinding = FragmentSettingGroupBinding.inflate(inflater)
        socialActivity = activity as SocialActivity

        val userDocumentId = socialActivity.getUserDocumentId()
        if (userDocumentId != null) {
            viewModel.userDocumentId = userDocumentId
            viewModel.gettingGroupCode()
        } else {
            Log.e("SettingGroupFragment", "userDocumentId is null")
        }

        settingToolbar()

        setupGroupOptions()

        setupObservers()

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

    // LiveData 관찰
    private fun setupObservers() {
        // 그룹 코드
        viewModel.groupCode.observe(viewLifecycleOwner) { group ->
            fragmentSettingGroupBinding.settingGroupCode.text = group
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
            onPositiveClick =  {
                viewModel.exitGroup(
                onSuccess = {
                    // 그룹 탈퇴 성공 시 GroupActivity로 이동
                    val intent = Intent(requireContext(), GroupActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                },
                onFailure = { e ->
                    Log.e("SettingGroupFragment", "그룹 나가기 실패: ${e.message}")
                    // 실패 시 추가 처리 (예: 토스트 메시지 등)
                }
            )
    },
            negativeText = "아니오",
            onNegativeClick = {

            }
        )
        dialog.showCustomDialog()
    }
}