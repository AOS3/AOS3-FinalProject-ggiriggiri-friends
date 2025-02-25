package com.friends.ggiriggiri.ui.fourth.modifygrouppw


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
import com.friends.ggiriggiri.GroupActivity
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.data.model.GroupModel
import com.friends.ggiriggiri.databinding.FragmentModifyGroupPwBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.first.register.UserModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ModifyGroupPwFragment : Fragment() {

    lateinit var fragmentModifyGroupPwBinding: FragmentModifyGroupPwBinding
    lateinit var socialActivity: SocialActivity

    private val modifyGroupPwViewModel: ModifyGroupPwViewModel by viewModels()

    // 사용자 정보를 담을 변수
    lateinit var userModel: UserModel

    // 그룹 정보를 담을 변수
    lateinit var groupModel: GroupModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentModifyGroupPwBinding = FragmentModifyGroupPwBinding.inflate(inflater)
        socialActivity = activity as SocialActivity

        // SocialActivity에서 로그인 사용자 정보를 가져와 userModel 초기화
        val loginUser = (socialActivity.application as App).loginUserModel
        userModel = loginUser // 로그인된 사용자 정보를 기반으로 초기화

        val userDocumentId = socialActivity.getUserDocumentId()
        if (userDocumentId != null) {
            modifyGroupPwViewModel.userDocumentId = userDocumentId
        } else {
            Log.e("ModifyGroupPwFragment", "userDocumentId is null")
        }

        groupModel = GroupModel()

        lifecycleScope.launch {
            try {
                groupModel = modifyGroupPwViewModel.service.selectGroupDataByGroupDocumentIdOne(userModel.userGroupDocumentID)
                modifyGroupPwViewModel.groupDocumentId = userModel.userGroupDocumentID

            } catch (e: Exception) {
                Log.e("ModifyGroupPwFragment", "그룹 데이터 가져오기 실패: ${e.message}")
            }
        }

        settingToolbar()

        setupObservers()
        setupTextWatchers()

        applyButton()

        return fragmentModifyGroupPwBinding.root
    }

    // Toolbar
    private fun settingToolbar(){
        fragmentModifyGroupPwBinding.apply {
            toolbarModifyGroupPw.setTitle("그룹 비밀번호 재설정")
            toolbarModifyGroupPw.setNavigationIcon(R.drawable.ic_arrow_back_ios)
            toolbarModifyGroupPw.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun setupObservers() {
        // 새 비밀번호 에러 메시지 관찰
        modifyGroupPwViewModel.newPwErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
            fragmentModifyGroupPwBinding.modifyGroupPwTextField.error = errorMessage
        }

        // 비밀번호 확인 에러 메시지 관찰
        modifyGroupPwViewModel.confirmPwErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
            fragmentModifyGroupPwBinding.modifyGroupPwCheckTextField.error = errorMessage
        }

        // 버튼 활성화 상태 관찰
        modifyGroupPwViewModel.isButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            fragmentModifyGroupPwBinding.modifyGroupPwButton.isEnabled = isEnabled
        }
    }

    private fun setupTextWatchers() {

        // 새 비밀번호 입력 감지
        fragmentModifyGroupPwBinding.modifyGroupPwTextField.editText?.addTextChangedListener { text ->
            modifyGroupPwViewModel.newPw.value = text.toString()
            modifyGroupPwViewModel.validateCurrentPw()
        }

        // 비밀번호 확인 입력 감지
        fragmentModifyGroupPwBinding.modifyGroupPwCheckTextField.editText?.addTextChangedListener { text ->
            modifyGroupPwViewModel.confirmPw.value = text.toString()
            modifyGroupPwViewModel.validateConfirmPw()
        }
    }

    // button
    private fun applyButton(){
        fragmentModifyGroupPwBinding.apply {
            modifyGroupPwButton.setOnClickListener {
                modifyGroupPwDialog()
            }
        }
    }

    // 그룹 비밀번호 재설정 다이얼로그\
    private fun modifyGroupPwDialog(){
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "그룹 비밀번호를 변경하시겠습니까?",
            icon = R.drawable.ic_logout,
            positiveText = "예",
            onPositiveClick = {
                // 비밀번호 변경 로직 실행
                CoroutineScope(Dispatchers.Main).launch {
                    val pw = modifyGroupPwViewModel.newPw.value!!
                    if(pw.isNotEmpty()){
                        groupModel.groupPw = pw
                    }
                    try {
                        modifyGroupPwViewModel.service.updateGroupPw(groupModel)
                        Toast.makeText(requireContext(), "그룹비밀번호가 변경 되었습니다.", Toast.LENGTH_SHORT).show()
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