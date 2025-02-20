package com.friends.ggiriggiri.ui.fourth.modifygroupname


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentModifyGroupNameBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog

class ModifyGroupNameFragment : Fragment() {

    lateinit var fragmentModifyGroupNameBinding: FragmentModifyGroupNameBinding
    lateinit var socialActivity: SocialActivity
    private val modifyGroupNameViewModel: ModifyGroupNameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentModifyGroupNameBinding = FragmentModifyGroupNameBinding.inflate(layoutInflater)
        socialActivity = activity as SocialActivity

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
        // 새 그룹명 에러 메시지 관찰
        modifyGroupNameViewModel.newGroupNameError.observe(viewLifecycleOwner) { errorMessage ->
            fragmentModifyGroupNameBinding.modifyGroupNameTextField.error = errorMessage
        }

        // 버튼 활성화 상태 관찰
        modifyGroupNameViewModel.isButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            fragmentModifyGroupNameBinding.modifyGroupNameButton.isEnabled = isEnabled
        }
    }

    private fun setupTextWatchers() {
        // 새 그룹명 입력 감지
        fragmentModifyGroupNameBinding.modifyGroupNameTextField.editText?.addTextChangedListener { text ->
            modifyGroupNameViewModel.newGroupName.value = text.toString()
            modifyGroupNameViewModel.validateNewName()
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

            },
            negativeText = "아니오",
            onNegativeClick = {

            }
        )
        dialog.showCustomDialog()
    }
}