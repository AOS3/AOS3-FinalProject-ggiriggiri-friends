package com.friends.ggiriggiri.ui.second.joingroup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentJoinGroupBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinGroupFragment : Fragment() {

    lateinit var fragmentJoinGroupBinding: FragmentJoinGroupBinding
    private val joinGroupViewModel: JoinGroupViewModel by viewModels()
    private lateinit var progressDialog: CustomDialogProgressbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentJoinGroupBinding = FragmentJoinGroupBinding.inflate(inflater, container, false)

        progressDialog = CustomDialogProgressbar(requireContext())

        enterGroup()
        // Inflate the layout for this fragment
        return fragmentJoinGroupBinding.root
    }

    private fun enterGroup(){
        fragmentJoinGroupBinding.apply {
            btnJoinGroupJoin.setOnClickListener {
                val userEmail = (requireActivity().application as App).loginUserModel.userId
                val groupCode = tfJoinGroupCode.editText?.text.toString().trim()
                val groupPw = tfJoinGroupPassword.editText?.text.toString().trim()

                if (groupCode.isEmpty()) {
                    tfJoinGroupCode.error = "그룹 코드를 입력해주세요."
                    return@setOnClickListener
                } else {
                    tfJoinGroupCode.helperText = " "
                }

                if (groupPw.isEmpty()) {
                    tfJoinGroupPassword.error = "비밀번호를 입력해주세요."
                    return@setOnClickListener
                } else {
                    tfJoinGroupPassword.helperText = " "
                }

                progressDialog.show()

                joinGroupViewModel.joinGroup(userEmail, groupCode, groupPw) { isSuccess ->
                    progressDialog.dismiss()
                    if (isSuccess) {
                        val app = requireActivity().application as App
                        joinGroupViewModel.getUserGroupDocumentID(app.loginUserModel.userDocumentId) { userGroupDocumentID ->
                            app.loginUserModel.userGroupDocumentID = userGroupDocumentID ?: ""

                            Log.d("JoinGroupFragment", "업데이트된 userGroupDocumentID: ${app.loginUserModel.userGroupDocumentID}")

                            val intent = Intent(requireContext(), SocialActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    } else {
                        noInfoGroupDialog()
                    }
                }
            }
        }
    }

    // 해당 정보를 가진 그룹이 없는 다이얼로그
    private fun noInfoGroupDialog(){
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "해당 정보를 가진 그룹이 없습니다.",
            icon = R.drawable.ic_error, // 아이콘 리소스
            positiveText = "확인",
            onPositiveClick = {
                // 확인 누를시 작동
            },
        )
        dialog.showCustomDialog()
    }


}