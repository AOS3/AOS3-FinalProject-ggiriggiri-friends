package com.friends.ggiriggiri.ui.start.findpw

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentFindPwBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindPwFragment : Fragment() {
    private var _binding: FragmentFindPwBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginActivity: LoginActivity
    private val findPwViewModel: FindPwViewModel by viewModels()

    // 휴대폰 번호 유효성 결과
    var isPhoneNumberValidResult = false

    // 휴대폰 번호 인증 통과 여부
    var isCertificationNumberValidResult = false

    // 비밀번호 찾기 할수있는지
    var canWeFindPwResult = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindPwBinding.inflate(inflater,container,false)
        loginActivity = activity as LoginActivity
        // 옵저버
        observeViewModel()

        //툴바세팅
        settingToolbar()

        //버튼 세팅
        settingButtons()

        //휴대폰번호 입력 유효성검사
        settingTilFindPwFragmentPhoneNumberNumber()

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
        findPwViewModel.apply {
            //전화번호 에러메세지
            phoneNumberErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.tilFindPwFragmentPhoneNumber.error = errorMessage
            }

            // 휴대폰 번호 유효성 결과
            isPhoneNumberValid.observe(viewLifecycleOwner) { it ->
                isPhoneNumberValidResult = it
            }

            //타이머설정
            tvFindPwFormattedTimeText.observe(viewLifecycleOwner) { time ->
                binding.tvFindPwFormattedTime.text = time
            }

            // 재인증 버튼으로 변화
            btnFindPwFragmentGetCertificationNumberText.observe(viewLifecycleOwner) { text ->
                binding.btnFindPwFragmentGetCertificationNumber.text = text
            }

            // 인증시간동안 재인증버튼 비활성화
            btnFindPwFragmentGetCertificationNumberEnabled.observe(viewLifecycleOwner) { enabled ->
                binding.btnFindPwFragmentGetCertificationNumber.isEnabled = enabled!!
            }

            // 인증번호 누르면 인증번호입력창 보이게
            llFindPwFragmentConfirmCertificationIsVisible.observe(viewLifecycleOwner) { visible ->
                binding.llFindPwFragmentConfirmCertification.visibility = visible
            }

            // 인증번호 누르면 인증번호 입력창 막히게하는 변수
            etFindPwFragmentPhoneNumberIsEnabled.observe(viewLifecycleOwner) { enabled ->
                binding.etFindPwFragmentPhoneNumber.isEnabled = enabled
            }

            // 인증번호 에러메세지
            certificationNumberErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.apply {
                    tilFindPwFragmentCertificationNumber.error = errorMessage
                    tvFindPwFragmentCertificationNumberValid.visibility = View.INVISIBLE
                    tilFindPwFragmentCertificationNumber.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.error_color)
                }
            }

            // 인증번호 에러색상
            certificationNumberErrorColor.observe(viewLifecycleOwner) { color ->
                binding.tilFindPwFragmentCertificationNumber.setErrorTextColor(
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
                )
            }

            // 인증 결과
            isCertificationNumberValid.observe(viewLifecycleOwner){ isValid->
                isCertificationNumberValidResult = isValid
                binding.apply {
                    if (isValid){
                        tilFindPwFragmentCertificationNumber.error = null
                        tvFindPwFragmentCertificationNumberValid.text = "인증 완료"
                        tvFindPwFragmentCertificationNumberValid.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                        tvFindPwFragmentCertificationNumberValid.visibility = View.VISIBLE
                        tilFindPwFragmentCertificationNumber.boxStrokeColor =
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.material_dynamic_neutral60
                            )
                    }else{
                        tvFindPwFragmentCertificationNumberValid.visibility = View.INVISIBLE
                    }
                }
            }

            // 인증 성공시 인증 입력창 막히게하는 변수
            etFindPwFragmentCertificationNumberIsEnabled.observe(viewLifecycleOwner){ boolean ->
                binding.etFindPwFragmentCertificationNumber.isEnabled = boolean
            }

            // 인증 성공시 인증확인버튼 비활성화
            btnFindPwFragmentConfirmCertificationNumberEnabled.observe(viewLifecycleOwner){ boolean ->
                binding.btnFindPwFragmentConfirmCertificationNumber.isEnabled = boolean
            }

            canWeFindPw.observe(viewLifecycleOwner) { canWeFindPw ->
                canWeFindPwResult = canWeFindPw
                if (canWeFindPwResult) {
                    processingFindPw()
                }
            }
        }
    }


    private fun processingFindPw() {
        binding.apply {
            val id = etFindPwFragmentId.text?.toString() ?: ""
            val phoneNumber = etFindPwFragmentPhoneNumber.text?.toString() ?: ""
            findPwViewModel.goToResetPw(loginActivity,id,phoneNumber)
        }
    }

    //휴대폰번호 입력 유효성검사
    private fun settingTilFindPwFragmentPhoneNumberNumber() {
        binding.etFindPwFragmentPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                findPwViewModel.etFindPwFragmentPhoneNumberWatcher(s.toString())
            }
        })
    }
    // 공통으로 사용할 뒤로가기 다이얼로그
    fun showBackPressedDialog() {
        val customDialog = CustomDialog(
            context = loginActivity,
            onPositiveClick = {
                loginActivity.removeFragment(LoginFragmentName.FIND_PW_FRAGMENT)
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
        binding.tbFindPwFragment.apply {
            title = "비밀번호 찾기"
            isTitleCentered = true
            setNavigationIcon(R.drawable.ic_arrow_back_ios)
            setNavigationOnClickListener {
                val customDialog = CustomDialog(
                    context = loginActivity,
                    onPositiveClick = {
                        loginActivity.removeFragment(LoginFragmentName.FIND_PW_FRAGMENT)
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
            // 비밀번호 재설정하기
            btnFindPwFragmentResetPw.setOnClickListener {
                val id = etFindPwFragmentId.text?.toString() ?: ""
                val phoneNumber = etFindPwFragmentPhoneNumber.text?.toString() ?: ""

                findPwViewModel.goToResetPw(loginActivity, id, phoneNumber)

                //loginActivity.replaceFragment(LoginFragmentName.RESET_PW_FRAGMENT,true,true,null)
            }

            // 인증번호 요청
            btnFindPwFragmentGetCertificationNumber.setOnClickListener {
                val phoneNumber = etFindPwFragmentPhoneNumber.text?.toString() ?: ""

                val first = phoneNumber.substring(0,3)
                val second = phoneNumber.substring(3,7)
                val third = phoneNumber.substring(7)

                val dialog = CustomDialog(
                    context = requireContext(),
                    onPositiveClick = {
                        findPwViewModel.btnFindPwFragmentGetCertificationNumberOnClick(loginActivity,phoneNumber)
                    },
                    positiveText = "확인",
                    onNegativeClick = {

                    },
                    negativeText = "취소",
                    contentText = "해당 번호로 인증을 진행하시겠습니까?\n" +
                            "진행하시면 2분간 다른 번호로 바꿀 수 없습니다.\n" +
                            "${first}-${second}-${third}",
                    icon = R.drawable.ic_error
                )
                dialog.showCustomDialog()
            }

            btnFindPwFragmentConfirmCertificationNumber.setOnClickListener {
                if (etFindPwFragmentCertificationNumber.text?.isNotEmpty() == true){
                    val certificationNumber = etFindPwFragmentCertificationNumber.text?.toString() ?: ""
                    findPwViewModel.btnFindPwFragmentConfirmCertificationNumberOnclick(certificationNumber)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}