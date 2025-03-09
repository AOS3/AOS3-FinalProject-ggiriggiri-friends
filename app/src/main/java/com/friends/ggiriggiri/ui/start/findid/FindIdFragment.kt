package com.friends.ggiriggiri.ui.start.findid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentFindIdBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindIdFragment : Fragment() {

    private var _binding: FragmentFindIdBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginActivity: LoginActivity
    private val findIdViewModel: FindIdViewModel by viewModels()

    // 아이디 찾기 할수있는지
    var canWeFindIdResult = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindIdBinding.inflate(inflater, container, false)
        loginActivity = activity as LoginActivity

        //툴바세팅
        settingToolbar()

        //버튼 세팅
        settingButtons()

        //뷰모델 관찰
        observeViewModel()


        return binding.root
    }

    //툴바세팅
    fun settingToolbar() {
        binding.tbFindIdFragment.apply {
            title = "아이디 찾기"
            isTitleCentered = true
            setNavigationIcon(R.drawable.ic_arrow_back_ios)
            setNavigationOnClickListener {
                loginActivity.removeFragment(LoginFragmentName.FIND_ID_FRAGMENT)
            }
        }
    }

    //뷰모델 관찰
    private fun observeViewModel() {
        binding.apply {
            findIdViewModel.canWeFindId.observe(viewLifecycleOwner) { canWeFindId ->
                canWeFindIdResult = canWeFindId
                if (canWeFindIdResult) {
                    processingFindId()
                }
            }
            findIdViewModel.nameErrorMessage.observe(viewLifecycleOwner) { nameErrorMessage ->
                tilFindIdFragmentName.error = nameErrorMessage

            }
            findIdViewModel.phoneNumberErrorMessage.observe(viewLifecycleOwner) { phoneNumberErrorMessage ->
                tilFindIdFragmentPhoneNumber.error = phoneNumberErrorMessage

            }
        }

    }

    private fun processingFindId() {
        binding.apply {
            val name = etFindIdFragmentName.text?.toString() ?: ""
            val phoneNumber = etFindIdFragmentPhoneNumber.text?.toString() ?: ""

            findIdViewModel.findId(loginActivity,name,phoneNumber)
        }
    }


    fun settingButtons() {
        binding.apply {
            // 아이디 찾기 완료
            btnFindIdFragmentFindId.setOnClickListener {

                val name = etFindIdFragmentName.text?.toString() ?: ""
                val phoneNumber = etFindIdFragmentPhoneNumber.text?.toString() ?: ""

                findIdViewModel.btnFindIdFragmentFindIdOnClick(name, phoneNumber)

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}