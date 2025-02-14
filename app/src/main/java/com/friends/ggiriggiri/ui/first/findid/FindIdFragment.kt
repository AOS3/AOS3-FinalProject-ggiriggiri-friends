package com.friends.ggiriggiri.ui.first.findid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.LoginFragmentName
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentFindIdBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import kotlin.math.log

class FindIdFragment : Fragment() {
    lateinit var fragmentFindIdBinding: FragmentFindIdBinding
    lateinit var loginActivity: LoginActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentFindIdBinding = FragmentFindIdBinding.inflate(inflater,container,false)
        loginActivity = activity as LoginActivity

        //툴바세팅
        settingToolbar()

        //버튼 세팅
        settingButtons()

        return fragmentFindIdBinding.root
    }

    //툴바세팅
    fun settingToolbar() {
        fragmentFindIdBinding.tbFindIdFragment.apply {
            title = "아이디 찾기"
            isTitleCentered = true
            setNavigationIcon(R.drawable.ic_arrow_back_ios)
            setNavigationOnClickListener {
                loginActivity.removeFragment(LoginFragmentName.FIND_ID_FRAGMENT)
            }
        }
    }

    fun settingButtons() {
        fragmentFindIdBinding.apply {
            // 아이디 찾기 완료
            btnFindIdFragmentFindId.setOnClickListener {

                val customDialog = CustomDialog(
                    context = loginActivity,
                    onPositiveClick = {
                        loginActivity.removeFragment(LoginFragmentName.FIND_ID_FRAGMENT)
                    },
                    positiveText = "로그인",
                    onNegativeClick = {
                        loginActivity.replaceFragment(LoginFragmentName.FIND_PW_FRAGMENT,false,true,null)
                    },
                    negativeText = "비밀번호찾기",
                    contentText = "채수범님의 아이디는 cssbbb입니다",
                    icon = R.drawable.ic_check_circle
                )

                customDialog.showCustomDialog()
            }
        }
    }


}