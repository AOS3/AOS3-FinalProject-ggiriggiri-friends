package com.friends.ggiriggiri.ui.fourth.modifyuserpw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentModifyUserPwBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog

class ModifyUserPwFragment : Fragment() {

    lateinit var fragmentModifyUserPwBinding: FragmentModifyUserPwBinding
    lateinit var socialActivity: SocialActivity
    private val modifyUserViewModel: ModifyUserPwViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentModifyUserPwBinding = FragmentModifyUserPwBinding.inflate(inflater)
        socialActivity = activity as SocialActivity

        settingToolbar()

        setupObservers()
        setupTextWatchers()

        applyButton()

        return fragmentModifyUserPwBinding.root
    }

    // Toolbar
    private fun settingToolbar(){
        fragmentModifyUserPwBinding.apply {
            toolbarModifyGroupPw.setTitle("비밀번호 변경")
            toolbarModifyGroupPw.setNavigationIcon(R.drawable.ic_arrow_back_ios)
            toolbarModifyGroupPw.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun setupObservers() {
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