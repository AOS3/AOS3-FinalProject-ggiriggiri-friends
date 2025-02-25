package com.friends.ggiriggiri.ui.fourth.modifyuserpw

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.data.repository.MyPageRepository
import com.friends.ggiriggiri.data.service.MyPageService
import com.friends.ggiriggiri.databinding.FragmentModifyUserPwBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.first.register.UserModel
import com.friends.ggiriggiri.ui.first.register.UserService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.user.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ModifyUserPwFragment : Fragment() {

    lateinit var fragmentModifyUserPwBinding: FragmentModifyUserPwBinding
    lateinit var socialActivity: SocialActivity

    private val modifyUserViewModel: ModifyUserPwViewModel by viewModels()

    // 사용자 정보를 담을 변수
    lateinit var userModel: UserModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentModifyUserPwBinding = FragmentModifyUserPwBinding.inflate(inflater)
        socialActivity = activity as SocialActivity

        // SocialActivity에서 로그인 사용자 정보를 가져와 userModel 초기화
        val loginUser = (socialActivity.application as App).loginUserModel
        userModel = loginUser // 로그인된 사용자 정보를 기반으로 초기화

        val userDocumentId = socialActivity.getUserDocumentId()
        if (userDocumentId != null) {
            modifyUserViewModel.userDocumentId = userDocumentId
        } else {
            Log.e("ModifyUserPwFragment", "userDocumentId is null")
        }

        settingToolbar()

        setupObservers()
        setupTextWatchers()

        applyButton()

        return fragmentModifyUserPwBinding.root
    }

    // Toolbar
    private fun settingToolbar() {
        fragmentModifyUserPwBinding.apply {
            toolbarModifyGroupPw.setTitle("비밀번호 변경")
            toolbarModifyGroupPw.setNavigationIcon(R.drawable.ic_arrow_back_ios)
            toolbarModifyGroupPw.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun setupObservers() {
        // 현재 비밀번호 에러 메시지 관찰
        modifyUserViewModel.pwErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
            fragmentModifyUserPwBinding.modifyUserPwTextField.error = errorMessage
        }

        // 새 비밀번호 에러 메시지 관찰
        modifyUserViewModel.newPwErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
            fragmentModifyUserPwBinding.modifyUserNewPwTextField.error = errorMessage
        }

        // 비밀번호 확인 에러 메시지 관찰
        modifyUserViewModel.confirmPwErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
            fragmentModifyUserPwBinding.modifyUserPwCheckTextField.error = errorMessage
        }

        // 버튼 활성화 상태 관찰
        modifyUserViewModel.isButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            fragmentModifyUserPwBinding.modifyUserPwButton.isEnabled = isEnabled
        }
    }

    private fun setupTextWatchers() {

        // 현재 비밀번호 입력 감지
        fragmentModifyUserPwBinding.modifyUserPwTextField.editText?.addTextChangedListener { text ->
            modifyUserViewModel.userPw.value = text.toString()
            modifyUserViewModel.validateCurrentPw()
        }

        // 새 비밀번호 입력 감지
        fragmentModifyUserPwBinding.modifyUserNewPwTextField.editText?.addTextChangedListener { text ->
            modifyUserViewModel.newPw.value = text.toString()
            modifyUserViewModel.validateNewPw()
        }

        // 비밀번호 확인 입력 감지
        fragmentModifyUserPwBinding.modifyUserPwCheckTextField.editText?.addTextChangedListener { text ->
            modifyUserViewModel.confirmPw.value = text.toString()
            modifyUserViewModel.validateConfirmPw()
        }
    }

    // button
    private fun applyButton() {
        fragmentModifyUserPwBinding.apply {
            modifyUserPwButton.setOnClickListener {
                modifyUserPwDialog()
            }
        }
    }

    // 유저 비밀번호 변경 다이얼 로그
    private fun modifyUserPwDialog() {
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "비밀번호를 변경하시겠습니까?",
            icon = R.drawable.ic_logout,
            positiveText = "예",
            onPositiveClick = {
                // 비밀번호 변경 로직 실행
                CoroutineScope(Dispatchers.Main).launch {

                    val pw = modifyUserViewModel.newPw.value!!
                    if(pw.isNotEmpty()){
                        userModel.userPw = pw
                    }

                    try {
                        modifyUserViewModel.myPageService.updateUserPw(userModel)
                        Toast.makeText(requireContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "비밀번호 변경 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        Log.e("ModifyPassword", "Error updating password", e)
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
