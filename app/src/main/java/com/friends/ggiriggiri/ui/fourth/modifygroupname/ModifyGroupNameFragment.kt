package com.friends.ggiriggiri.ui.fourth.modifygroupname


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.data.model.GroupModel2
import com.friends.ggiriggiri.databinding.FragmentModifyGroupNameBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.first.register.UserModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ModifyGroupNameFragment : Fragment() {

    lateinit var fragmentModifyGroupNameBinding: FragmentModifyGroupNameBinding
    lateinit var socialActivity: SocialActivity

    private val modifyGroupNameViewModel: ModifyGroupNameViewModel by viewModels()

    // 사용자 정보를 담을 변수
    lateinit var userModel: UserModel

    // 그룹 정보를 담을 변수
    lateinit var groupModel: GroupModel2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentModifyGroupNameBinding = FragmentModifyGroupNameBinding.inflate(layoutInflater)
        socialActivity = activity as SocialActivity

        // SocialActivity에서 로그인 사용자 정보를 가져와 userModel 초기화
        val loginUser = (socialActivity.application as App).loginUserModel
        userModel = loginUser // 로그인된 사용자 정보를 기반으로 초기화

        val userDocumentId = socialActivity.getUserDocumentId()
        if (userDocumentId != null) {
            modifyGroupNameViewModel.userDocumentId = userDocumentId
        } else {
            Log.e("ModifyGroupPwFragment", "userDocumentId is null")
        }

        groupModel = GroupModel2()

        lifecycleScope.launch {
            try {
                groupModel = modifyGroupNameViewModel.service.selectGroupDataByGroupDocumentIdOne(userModel.userGroupDocumentID)
                modifyGroupNameViewModel.groupDocumentId = userModel.userGroupDocumentID
            } catch (e: Exception) {
                Log.e("ModifyGroupPwFragment", "그룹 데이터 가져오기 실패: ${e.message}")
            }
        }

        settingToolbar()

        setupObservers()
        setupTextWatchers()

        applyButton()
        return fragmentModifyGroupNameBinding.root
    }
    // Toolbar
    private fun settingToolbar(){
        fragmentModifyGroupNameBinding.apply {
            toolbarModifyGroupName.setTitle("그룹명 변경")
            toolbarModifyGroupName.setNavigationIcon(R.drawable.ic_arrow_back_ios)
            toolbarModifyGroupName.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun setupObservers() {
        // 그룹명 에러 메시지 관찰
        modifyGroupNameViewModel.groupNameErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
            fragmentModifyGroupNameBinding.modifyGroupNameTextField.error = errorMessage
        }

        // 버튼 활성화 상태 관찰
        modifyGroupNameViewModel.isButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            fragmentModifyGroupNameBinding.modifyGroupNameButton.isEnabled = isEnabled
        }
    }

    private fun setupTextWatchers() {
        //  그룹명 입력 감지
        fragmentModifyGroupNameBinding.modifyGroupNameTextField.editText?.addTextChangedListener { text ->
            modifyGroupNameViewModel.groupName.value = text.toString()
            modifyGroupNameViewModel.validateCurrentName()
        }

    }

    // button
    private fun applyButton(){
        fragmentModifyGroupNameBinding.apply {
            modifyGroupNameButton.setOnClickListener {
                modifyGroupNameDialog()
            }
        }
    }



    // 그룹명 변경 커스텀 다이얼로그
    private fun modifyGroupNameDialog(){
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "그룹명을 변경하시겠습니까?",
            icon = R.drawable.ic_edit,
            positiveText = "예",
            onPositiveClick = {
                // 그룹명 변경 로직 실행
                CoroutineScope(Dispatchers.Main).launch {
                    val name = modifyGroupNameViewModel.groupName.value!!
                    if(name.isNotEmpty()){
                        groupModel.groupName = name
                    }
                    try {
                        modifyGroupNameViewModel.service.updateGroupName(groupModel)
                        Toast.makeText(requireContext(), "그룹명이 변경 되었습니다.", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } catch (e: Exception){
                        Log.e("modifyGroupPwDialog", "Error updating password", e)
                    }
                }
            },
            negativeText = "아니오",
            onNegativeClick = {

            }
        )
        dialog.showCustomDialog()
    }
}