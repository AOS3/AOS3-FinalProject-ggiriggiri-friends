package com.friends.ggiriggiri.ui.fourth.settinggroup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentSettingGroupBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.fourth.modifygroupname.ModifyGroupNameFragment
import com.friends.ggiriggiri.ui.fourth.modifygrouppw.ModifyGroupPwFragment
import com.friends.ggiriggiri.ui.fourth.modifyuserpw.ModifyUserPwFragment
import com.friends.ggiriggiri.ui.fourth.mypage.MyPageFragment
import com.friends.ggiriggiri.ui.fourth.mypage.MyPageFragmentName
import com.google.android.material.transition.MaterialSharedAxis

class SettingGroupFragment : Fragment() {

    lateinit var fragmentSettingGroupBinding: FragmentSettingGroupBinding
    lateinit var socialActivity: SocialActivity

    // 현재 Fragment와 다음 Fragment를 담을 변수(애니메이션 이동 때문에...)
    var newFragment: Fragment? = null
    var oldFragment: Fragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentSettingGroupBinding = FragmentSettingGroupBinding.inflate(inflater)
        socialActivity = activity as SocialActivity

        settingToolbar()

        setupGroupOptions()

        return fragmentSettingGroupBinding.root
    }

    // Toolbar
    private fun settingToolbar(){
        fragmentSettingGroupBinding.apply {
            toolbarGroupSetting.setTitle("그룹 설정")
            toolbarGroupSetting.setNavigationIcon(R.drawable.ic_arrow_back_ios)
            toolbarGroupSetting.setNavigationOnClickListener {
                socialActivity.supportFragmentManager.popBackStack()
            }
        }
    }


    // 각 텍스트 동작 함수
    private fun setupGroupOptions() {
        fragmentSettingGroupBinding.apply {
            modifyGroupPw.text = "그룹 비밀번호 변경"
            modifyGroupName.text = "그룹명 변경"
            modifyGroupExit.text = "그룹 나가기"
            val groupItemClickListener = View.OnClickListener { view ->
                when (view.id) {
                    R.id.modifyGroupPw -> {
                        // 그룹 비밀번호 변경 동작 구현
                        replaceFragment(SettingGroupFragmentName.MODIFY_GROUP_PW_FRAGMENT,true,false,null)
                    }

                    R.id.modifyGroupName -> {
                        // 그룹명 변경 동작 구현
                        replaceFragment(SettingGroupFragmentName.MODIFY_GROUP_NAME_FRAGMENT,true,false,null)

                    }

                    R.id.modifyGroupExit -> {
                        modifyGroupExitDialog()
                    }
                }
            }
            modifyGroupPw.setOnClickListener(groupItemClickListener)
            modifyGroupName.setOnClickListener(groupItemClickListener)
            modifyGroupExit.setOnClickListener(groupItemClickListener)
        }
    }

    // 그룹 나가기 다이얼로그
    private fun modifyGroupExitDialog(){
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "정말 그룹을 나가시겠습니까?",
            icon = R.drawable.ic_group_off,
            positiveText = "예",
            onPositiveClick = {

            },
            negativeText = "아니오",
            onNegativeClick = {

            }
        )
        dialog.showCustomDialog()
    }


    // 프래그먼트를 교체하는 함수
    fun replaceFragment(fragmentName: SettingGroupFragmentName, isAddToBackStack:Boolean, animate:Boolean, dataBundle: Bundle?){

        // 프래그먼트 객체
        newFragment = when(fragmentName){
            // 그룹명 변경 화면
            SettingGroupFragmentName.MODIFY_GROUP_NAME_FRAGMENT -> ModifyGroupNameFragment()
            // 그룹 비밀번호 변경 화면
            SettingGroupFragmentName.MODIFY_GROUP_PW_FRAGMENT -> ModifyGroupPwFragment()
        }

        // bundle 객체가 null이 아니라면
        if(dataBundle != null){
            newFragment?.arguments = dataBundle
        }

        // 프래그먼트 교체
        socialActivity.supportFragmentManager.commit {

            if(animate) {
                // 만약 이전 프래그먼트가 있다면
                if(oldFragment != null){
                    oldFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                    oldFragment?.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
                }

                newFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                newFragment?.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
                newFragment?.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                newFragment?.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
            }

            replace(R.id.fragmentContainerViewSocialMain, newFragment!!)
            if(isAddToBackStack){
                addToBackStack(fragmentName.str)
            }
        }
    }
}

// MYPAGE 프래그먼트
enum class SettingGroupFragmentName(var number:Int, var str:String){
    // 그룹명 변경
    MODIFY_GROUP_NAME_FRAGMENT(1,"ModifyGroupNameFragment"),
    // 그룹 비밀번호 변경
    MODIFY_GROUP_PW_FRAGMENT(2,"ModifyGroupPwFragment")
}