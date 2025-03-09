package com.friends.ggiriggiri.ui.start.findpw

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentResetPwBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPwFragment : Fragment() {
    private var _binding: FragmentResetPwBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginActivity: LoginActivity
    private val resetPwViewModel: ResetPwViewModel by viewModels()

    //사용자 docimentId
    var userDocumentId = ""

    //비밀번호 바꾸는거 가능
    var isPwValidResult = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPwBinding.inflate(inflater,container,false)
        loginActivity = activity as LoginActivity

        //툴바세팅
        settingToolbar()

        //버튼 세팅
        settingButtons()

        // 유저 id 받아오기
        gettingArguments()

        // 비밀번호1 text watcher
        settingTilResetPwFragmentNewPw1()

        // 비밀번호2 text watcher
        settingTilResetPwFragmentNewPw2()

        // 옵저버
        observeViewModel()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showBackPressedDialog()
            }
        })
    }
    // 옵저버
    private fun observeViewModel() {
        resetPwViewModel.apply {
            //비밀번호 에러메세지
            pw1ErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.tilResetPwFragmentNewPw1.error = errorMessage
            }

            //비밀번호 에러메세지
            pw2ErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.tilResetPwFragmentNewPw2.error = errorMessage
            }

            //isValid
            isPwValid.observe(viewLifecycleOwner) { boolean ->
                isPwValidResult = boolean
            }
        }
    }


    // 유저 id 받아오기
    fun gettingArguments(){
        userDocumentId = arguments?.getString("userDocumentId").toString()
        Log.d("test",userDocumentId)
    }
    // 공통으로 사용할 뒤로가기 다이얼로그
    fun showBackPressedDialog() {
        val customDialog = CustomDialog(
            context = loginActivity,
            onPositiveClick = {
                loginActivity.removeFragment(LoginFragmentName.FIND_PW_FRAGMENT)
                loginActivity.removeFragment(LoginFragmentName.RESET_PW_FRAGMENT)
            },
            positiveText = "확인",
            onNegativeClick = {},
            negativeText = "취소",
            contentText = "정말 뒤로 가시겠습니까?\n로그인 화면으로 이동합니다",
            icon = R.drawable.ic_error
        )
        customDialog.showCustomDialog()
    }

    //툴바세팅
    fun settingToolbar() {
        binding.tbResetPwFragment.apply {
            title = "비밀번호 재설정"
            isTitleCentered = true
            setNavigationIcon(R.drawable.ic_arrow_back_ios)
            setNavigationOnClickListener {
                val customDialog = CustomDialog(
                    context = loginActivity,
                    onPositiveClick = {
                        loginActivity.removeFragment(LoginFragmentName.FIND_PW_FRAGMENT)
                        loginActivity.removeFragment(LoginFragmentName.RESET_PW_FRAGMENT)
                    },
                    positiveText = "확인",
                    onNegativeClick = {},
                    negativeText = "취소",
                    contentText = "정말 뒤로 가시겠습니까?\n로그인 화면으로 이동합니다",
                    icon = R.drawable.ic_error
                )

                customDialog.showCustomDialog()
            }
        }
    }

    //버튼 세팅
    fun settingButtons() {
        binding.apply {
            // 비빌번호 재설정 완료
            btnResetPwFragmentResetPw.setOnClickListener {
                if (isPwValidResult == false){
                    Toast.makeText(loginActivity,"입력이 유효하지 않습니다",Toast.LENGTH_LONG).show()
                }else{
                    val customDialog = CustomDialog(
                        context = loginActivity,
                        onPositiveClick = {

                            //val pw1 = etResetPwFragmentNewPw1.text?.toString() ?: ""
                            val pw2 = etResetPwFragmentNewPw2.text?.toString() ?: ""

                            resetPwViewModel.resetPw(loginActivity,userDocumentId,pw2)

                        },
                        positiveText = "변경하기",
                        onNegativeClick = {},
                        negativeText = "취소",
                        contentText = "비밀번호를 재설정 하시겠습니까?\n로그인 화면으로 이동합니다",
                        icon = R.drawable.ic_edit
                    )

                    customDialog.showCustomDialog()
                }

            }
        }
    }

    // 비밀번호1 text watcher
    private fun settingTilResetPwFragmentNewPw1() {
        binding.apply {
            etResetPwFragmentNewPw1.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    resetPwViewModel.etResetPwFragmentNewPw1Watcher(s.toString())
                }
            })
        }
    }

    // 비밀번호2 text watcher
    private fun settingTilResetPwFragmentNewPw2() {
        binding.apply {
            etResetPwFragmentNewPw2.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    resetPwViewModel.etResetPwFragmentNewPw2Watcher(s.toString())
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}