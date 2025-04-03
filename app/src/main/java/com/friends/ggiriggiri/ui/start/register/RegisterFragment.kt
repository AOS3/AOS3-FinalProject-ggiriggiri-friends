package com.friends.ggiriggiri.ui.start.register

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentRegisterBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.custom.CustomLoginDialog2
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {


    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginActivity: LoginActivity
    private val registerViewModel: RegisterViewModel by viewModels()

    // 아이디 중복검사 통과 여부
    var isIdValidResult = false

    // 휴대폰 번호 유효성 결과
    var isPhoneNumberValidResult = false

    // 휴대폰 번호 인증 통과 여부
    var isCertificationNumberValidResult = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        loginActivity = activity as LoginActivity

        observeViewModel()

        //툴바세팅
        settingToolbar()

        //버튼 세팅
        settingButtons()

        //이름 유효성 검사
        settingTilRegisterFragmentName()

        //아이디 중복확인 유지
        settingTilRegisterFragmentId()

        //비밀번호 유효성 검사
        settingTilRegisterFragmentPw12()

        //전화번호 입력
        settingTilRegisterFragmentPhoneNumber()


        return binding.root
    }

    private fun observeViewModel() {
        registerViewModel.apply {
            //이름
            nameErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.tilRegisterFragmentName.error = errorMessage
            }

            // 아이디 에러 색상
            idErrorColor.observe(viewLifecycleOwner) { color ->
                binding.tilRegisterFragmentId.setErrorTextColor(
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
                )
            }
            //아이디 에러 메세지
            idErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.apply {
                    tilRegisterFragmentId.error = errorMessage
                    tvRegisterFragmentIdValid.visibility = View.INVISIBLE
                    tilRegisterFragmentId.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.error_color)
                }
            }

            //아이디 중복검사 통과 결과
            isIdValid.observe(viewLifecycleOwner) { isValid ->
                isIdValidResult = isValid
                binding.apply {
                    if (isValid) {
                        tilRegisterFragmentId.error = null  // 에러 해제
                        tvRegisterFragmentIdValid.text = "중복확인 완료"
                        tvRegisterFragmentIdValid.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                        tvRegisterFragmentIdValid.visibility = View.VISIBLE
                        tilRegisterFragmentId.boxStrokeColor =
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.material_dynamic_neutral60
                            )
                    } else {
                        tvRegisterFragmentIdValid.visibility = View.INVISIBLE
                    }
                }
            }

            //비밀번호1
            pw1ErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.tilRegisterFragmentPw1.error = errorMessage
            }
            //비밀번호2
            pw2ErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.tilRegisterFragmentPw2.error = errorMessage
            }

            //전화번호
            phoneNumberErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.tilRegisterFragmentPhoneNumber.error = errorMessage
            }

            // 휴대폰 번호 유효성 결과
            isPhoneNumberValid.observe(viewLifecycleOwner) { it ->
                isPhoneNumberValidResult = it
            }

            //타이머설정
            tvFormattedTime.observe(viewLifecycleOwner) { time ->
                binding.tvFormattedTime.text = time
            }

            // 재인증 버튼으로 변화
            btnRegisterFragmentGetCertificationNumberText.observe(viewLifecycleOwner) { text ->
                binding.btnRegisterFragmentGetCertificationNumber.text = text
            }

            // 인증시간동안 재인증버튼 비활성화
            btnRegisterFragmentGetCertificationNumberEnabled.observe(viewLifecycleOwner) { enabled ->
                binding.btnRegisterFragmentGetCertificationNumber.isEnabled = enabled!!
            }

            // 인증번호 누르면 인증번호입력창 보이게
            llRegisterFragmentConfirmCertificationIsVisible.observe(viewLifecycleOwner) { visible ->
                binding.llRegisterFragmentConfirmCertification.visibility = visible
            }

            // 인증번호 누르면 인증번호 입력창 막히게하는 변수
            etRegisterFragmentPhoneNumberIsEnabled.observe(viewLifecycleOwner) { enabled ->
                binding.etRegisterFragmentPhoneNumber.isEnabled = enabled
            }

            //인증번호 에러메세지
            certificationNumberErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                binding.apply {
                    tilRegisterFragmentCertificationNumber.error = errorMessage
                    tvRegisterFragmentCertificationNumberValid.visibility = View.INVISIBLE
                    tilRegisterFragmentCertificationNumber.boxStrokeColor =
                        ContextCompat.getColor(requireContext(), R.color.error_color)
                }
            }

            //인증번호 에러색상
            certificationNumberErrorColor.observe(viewLifecycleOwner) { color ->
                binding.tilRegisterFragmentCertificationNumber.setErrorTextColor(
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
                )
            }

            //인증 결과
            isCertificationNumberValid.observe(viewLifecycleOwner) { isValid ->
                isCertificationNumberValidResult = isValid
                binding.apply {
                    if (isValid) {
                        tilRegisterFragmentCertificationNumber.error = null
                        tvRegisterFragmentCertificationNumberValid.text = "인증 완료"
                        tvRegisterFragmentCertificationNumberValid.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                        tvRegisterFragmentCertificationNumberValid.visibility = View.VISIBLE
                        tilRegisterFragmentCertificationNumber.boxStrokeColor =
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.material_dynamic_neutral60
                            )
                    } else {
                        tvRegisterFragmentCertificationNumberValid.visibility = View.INVISIBLE
                    }
                }
            }

            // 인증 성공시 인증 입력창 막히게하는 변수
            etRegisterFragmentCertificationNumberIsEnabled.observe(viewLifecycleOwner) { boolean ->
                binding.etRegisterFragmentCertificationNumber.isEnabled = boolean
            }

            // 인증 성공시 인증확인버튼 비활성화
            btnRegisterFragmentConfirmCertificationNumberEnabled.observe(viewLifecycleOwner) { boolean ->
                binding.btnRegisterFragmentConfirmCertificationNumber.isEnabled = boolean
            }
        }
    }

    //툴바세팅
    fun settingToolbar() {
        binding.tbRegisterFragment.apply {
            title = "회원가입"
            isTitleCentered = true
            setNavigationIcon(R.drawable.ic_arrow_back_ios)
            setNavigationOnClickListener {
                loginActivity.removeFragment(LoginFragmentName.REGISTER_FRAGMENT)
            }
        }
    }

    //이름 유효성 검사
    private fun settingTilRegisterFragmentName() {
        binding.etRegisterFragmentName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // 텍스트가 변경되는 순간 호출
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                registerViewModel.etRegisterFragmentNameWatcher(s.toString())
            }
        })
    }

    //아이디 중복확인 유지
    private fun settingTilRegisterFragmentId() {
        binding.etRegisterFragmentId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // 텍스트가 변경되는 순간 호출
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                registerViewModel.etRegisterFragmentIdWatcher()
            }
        })
    }

    //비밀번호 유효성 검사
    private fun settingTilRegisterFragmentPw12() {
        binding.apply {
            etRegisterFragmentPw1.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                // 텍스트가 변경되는 순간 호출
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    registerViewModel.etRegisterFragmentPw1Watcher(s.toString())
                }
            })
            etRegisterFragmentPw2.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                // 텍스트가 변경되는 순간 호출
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    registerViewModel.etRegisterFragmentPw2Watcher(s.toString())
                }
            })

        }
    }

    //휴대폰번호 입력 유효성검사
    private fun settingTilRegisterFragmentPhoneNumber() {
        binding.etRegisterFragmentPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                registerViewModel.etRegisterFragmentPhoneNumberWatcher(s.toString())
            }
        })
    }

    // 회원가입 실행
    fun processingRegister() {
        binding.apply {
            val result = registerViewModel.btnRegisterFragmentSignupLoginOnClick()
            if (result) {

                val dialog1 = CustomLoginDialog2(
                    context = requireContext(),
                    onPositiveClick = {
                        val dialog2 = CustomLoginDialog2(
                            context = requireContext(),
                            onPositiveClick = {
                                val name = etRegisterFragmentName.text?.toString() ?: ""
                                val id = etRegisterFragmentId.text?.toString() ?: ""
                                val pw2 = etRegisterFragmentPw2.text?.toString() ?: ""
                                val phoneNumber =
                                    etRegisterFragmentPhoneNumber.text?.toString() ?: ""

                                val userModel = UserModel().apply {
                                    userId = id
                                    userName = name
                                    userPw = pw2
                                    userPhoneNumber = phoneNumber
                                    userJoinTime = System.currentTimeMillis()
                                }

                                // ViewModel에서 회원가입 실행
                                registerViewModel.registerUser(
                                    requireActivity() as LoginActivity,
                                    userModel
                                )
                            },
                            positiveText = "계속",
                            onNegativeClick = {},
                            negativeText = "취소",
                            contentText = "이용약관",
                            agreeTypeText = "이용약관에 동의합니다",
                            loadUrl = "https://sites.google.com/view/ggiriggiri-terms-of-use"
                        )
                        dialog2.showCustomDialog()
                    },
                    positiveText = "계속",
                    onNegativeClick = {},
                    negativeText = "취소",
                    contentText = "개인정보처리방침",
                    agreeTypeText = "개인정보처리방침에 동의합니다",
                    loadUrl = "https://sites.google.com/view/ggiriggiri-privacy-policy"
                )
                dialog1.showCustomDialog()
            } else {
                val dialog = CustomDialog(
                    context = requireContext(),
                    onPositiveClick = {},
                    positiveText = "확인",
                    onNegativeClick = {},
                    contentText = "모든 입력이 완벽하지 않습니다.",
                    icon = R.drawable.ic_error
                )
                dialog.showCustomDialog()
            }
        }
    }


    //버튼 세팅
    fun settingButtons() {
        binding.apply {
            //회원가입 완료
            btnRegisterFragmentSignup.setOnClickListener {
                val dialog = CustomDialog(
                    context = requireContext(),
                    onPositiveClick = {
                        // 회원가입실행
                        processingRegister()
                    },
                    positiveText = "확인",
                    onNegativeClick = {

                    },
                    negativeText = "취소",
                    contentText = "가입하시겠습니까?",
                    icon = R.drawable.ic_error
                )
                dialog.showCustomDialog()

            }

            //아이디 중복확인
            btnRegisterFragmentDuplicationCheck.setOnClickListener {

                val id = etRegisterFragmentId.text?.toString() ?: ""

                registerViewModel.btnRegisterFragmentDuplicationCheckOnClick(loginActivity, id)
            }

            //인증번호 요청
            btnRegisterFragmentGetCertificationNumber.setOnClickListener {
                if (isPhoneNumberValidResult) {
                    val phoneNumber = etRegisterFragmentPhoneNumber.text?.toString() ?: ""

                    val first = phoneNumber.substring(0, 3)
                    val second = phoneNumber.substring(3, 7)
                    val third = phoneNumber.substring(7)

                    val dialog = CustomDialog(
                        context = requireContext(),
                        onPositiveClick = {
                            registerViewModel.btnRegisterFragmentGetCertificationNumberOnClick(
                                loginActivity,
                                phoneNumber
                            )
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
            }

            //인증번호 확인
            btnRegisterFragmentConfirmCertificationNumber.setOnClickListener {
                if (etRegisterFragmentCertificationNumber.text?.isNotEmpty() == true) {
                    val certificationNumber =
                        etRegisterFragmentCertificationNumber.text?.toString() ?: ""
                    registerViewModel.btnRegisterFragmentConfirmCertificationNumberOnclick(
                        certificationNumber
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}