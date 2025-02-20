package com.friends.ggiriggiri.ui.fourth.modifygrouppw


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
import com.friends.ggiriggiri.databinding.FragmentModifyGroupPwBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog

class ModifyGroupPwFragment : Fragment() {

    lateinit var fragmentModifyGroupPwBinding: FragmentModifyGroupPwBinding
    lateinit var socialActivity: SocialActivity
    private val modifyGroupPwViewModel: ModifyGroupPwViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentModifyGroupPwBinding = FragmentModifyGroupPwBinding.inflate(inflater)
        socialActivity = activity as SocialActivity

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
                Toast.makeText(requireContext(), "그룹비밀번호가 변경 되었습니다.", Toast.LENGTH_SHORT).show()
            },
            negativeText = "아니오",
            onNegativeClick = {

            }
        )
        dialog.showCustomDialog()
    }
}