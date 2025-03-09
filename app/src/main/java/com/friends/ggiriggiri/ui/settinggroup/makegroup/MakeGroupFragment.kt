package com.friends.ggiriggiri.ui.settinggroup.makegroup

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentMakeGroupBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MakeGroupFragment : Fragment() {

    private lateinit var fragmentMakeGroupBinding: FragmentMakeGroupBinding

    private val makeGroupViewModel: MakeGroupViewModel by viewModels()
    private lateinit var progressDialog: CustomDialogProgressbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentMakeGroupBinding = FragmentMakeGroupBinding.inflate(inflater, container, false)

        progressDialog = CustomDialogProgressbar(requireContext())

        checkGroupCode()

        makeGroup()

        return fragmentMakeGroupBinding.root
    }

    // 중복확인 버튼 메서드
    private fun checkGroupCode() {
        fragmentMakeGroupBinding.btnMakeGroupCheckCode.setOnClickListener {
            val groupCode = fragmentMakeGroupBinding.tfMakeGroupGroupCode.editText?.text.toString().trim()

            if (groupCode.isEmpty()) {
                fragmentMakeGroupBinding.tfMakeGroupGroupCode.error = "그룹 코드를 입력해주세요."
                return@setOnClickListener
            }

            makeGroupViewModel.checkGroupCode(groupCode) { isAvailable ->
                if (isAvailable) {
                    showCheckCodeDialog()
                    fragmentMakeGroupBinding.tfMakeGroupGroupCode.helperText = " "
                } else {
                    usedGroupCodeDialog()
                }
            }
        }
    }

    // 그룹 생성 버튼
    private fun makeGroup() {
        fragmentMakeGroupBinding.btnMakeGroupMakeGroup.setOnClickListener {
            val userId = (requireActivity().application as App).loginUserModel.userId
            val groupName = fragmentMakeGroupBinding.tfMakeGroupGroupName.editText?.text.toString().trim()
            val groupCode = fragmentMakeGroupBinding.tfMakeGroupGroupCode.editText?.text.toString().trim()
            val groupPw = fragmentMakeGroupBinding.tfMakeGroupPassword1.editText?.text.toString().trim()
            val confirmPw = fragmentMakeGroupBinding.tfMakeGroupPassword2.editText?.text.toString().trim()

            // 유효성 검사
            if (groupName.isEmpty()) {
                fragmentMakeGroupBinding.tfMakeGroupGroupName.error = "그룹 명을 입력해주세요."
                return@setOnClickListener
            } else {
                fragmentMakeGroupBinding.tfMakeGroupGroupName.helperText = " "
            }

            if (groupCode.isEmpty()) {
                fragmentMakeGroupBinding.tfMakeGroupGroupCode.error = "그룹 코드를 입력해주세요."
                return@setOnClickListener
            } else {
                fragmentMakeGroupBinding.tfMakeGroupGroupCode.helperText = " "
            }

            if (groupPw.length < 6) {
                fragmentMakeGroupBinding.tfMakeGroupPassword1.error = "비밀번호는 6자리 이상이어야 합니다."
                return@setOnClickListener
            } else {
                fragmentMakeGroupBinding.tfMakeGroupPassword1.helperText = " "
            }

            if (groupPw != confirmPw) {
                fragmentMakeGroupBinding.tfMakeGroupPassword2.error = "비밀번호가 일치하지 않습니다."
                return@setOnClickListener
            } else {
                fragmentMakeGroupBinding.tfMakeGroupPassword2.helperText = " "
            }

            if (fragmentMakeGroupBinding.btnMakeGroupCheckCode.isEnabled) {
                fragmentMakeGroupBinding.tfMakeGroupGroupCode.error = "중복 확인을 해주세요"
                return@setOnClickListener
            } else {
                fragmentMakeGroupBinding.tfMakeGroupGroupCode.helperText = " "
            }

            if (!makeGroupViewModel.isGroupCodeAvailable) {
                fragmentMakeGroupBinding.tfMakeGroupGroupCode.error = "중복 확인을 해주세요."
                return@setOnClickListener
            } else {
                fragmentMakeGroupBinding.tfMakeGroupGroupCode.helperText = " "
            }

            progressDialog.show()

            // Firestore에 그룹 생성 요청
            makeGroupViewModel.createGroup(userId, groupName, groupCode, groupPw) { isSuccess ->
                progressDialog.dismiss()

                if (isSuccess) {
                    makeGroupSuccessDialog()
                } else {
                    Toast.makeText(requireContext(), "그룹 생성 실패! 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 중복확인 다이얼로그 표시 메서드
    private fun showCheckCodeDialog() {
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "사용 가능한 그룹 코드입니다.\n 사용하시겠습니까?",
            icon = R.drawable.ic_check_circle, // 아이콘 리소스
            positiveText = "확인",
            onPositiveClick = {
                fragmentMakeGroupBinding.btnMakeGroupCheckCode.isEnabled = false
                fragmentMakeGroupBinding.btnMakeGroupCheckCode.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#B2B1B4"))
                fragmentMakeGroupBinding.btnMakeGroupCheckCode.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                fragmentMakeGroupBinding.tfMakeGroupGroupCode.isEnabled = false
            },
            negativeText = "취소",
            onNegativeClick = {
                // 취소 버튼 클릭 시 동작
            }
        )
        dialog.showCustomDialog()
    }

    // 그룹이 생성 되었을때 다이얼로그
    private fun makeGroupSuccessDialog() {
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "그룹이 생성되었습니다.",
            icon = R.drawable.ic_check_circle,
            positiveText = "확인",
            onPositiveClick = {
                val app = requireActivity().application as App

                // Firestore에서 최신 값 가져와 `loginUserModel` 업데이트
                makeGroupViewModel.getUserGroupDocumentID(app.loginUserModel.userId) { userGroupDocumentID ->
                    app.loginUserModel.userGroupDocumentID = userGroupDocumentID ?: ""

                    Log.d("MakeGroupFragment", "✅ 업데이트된 userGroupDocumentID: ${app.loginUserModel.userGroupDocumentID}")

                    // 값이 정상적으로 반영된 후 `SocialActivity`로 이동
                    val intent = Intent(requireContext(), SocialActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        )

        dialog.showCustomDialog()
    }

    // 중복된 그룹코드가 있으면 나오는 다이얼로그
    private fun usedGroupCodeDialog() {
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "사용 불가능한 그룹 코드입니다",
            icon = R.drawable.ic_error, // 아이콘 리소스
            positiveText = "확인",
            onPositiveClick = {
            }
        )

        dialog.showCustomDialog()
    }

}