package com.friends.ggiriggiri.ui.fourth.mypage

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentMyPageBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.fourth.modifyuserpw.ModifyUserPwFragment
import com.friends.ggiriggiri.ui.fourth.settinggroup.SettingGroupFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.transition.MaterialSharedAxis

class MyPageFragment : Fragment() {

    private lateinit var fragmentMyPageBinding: FragmentMyPageBinding
    private lateinit var socialActivity: SocialActivity

    private var profileImageUri: Uri? = null // 프로필 이미지 URI 저장


    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ActivityResultLauncher 초기화
        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // 권한이 허용되었을 때
                navigateToNotificationSettings()
            } else {
                // 권한이 거부되었을 때
                navigateToNotificationSettings()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMyPageBinding = FragmentMyPageBinding.inflate(inflater)
        socialActivity = activity as SocialActivity



        settingToolbar()

        settingProfile()

        setupMyPageOptions()

        return fragmentMyPageBinding.root
    }

    // Toolbar
    private fun settingToolbar() {
        fragmentMyPageBinding.apply {
            toolbarMyPage.setTitle("마이페이지")
        }
    }

    // Profile 클릭시
    private fun settingProfile() {
        fragmentMyPageBinding.apply {
            profileImage.setOnClickListener {
                val myPageBottomSheetFragment = MyPageBottomSheetFragment()
                myPageBottomSheetFragment.show(
                    childFragmentManager,
                    "MyPageBottomSheet"
                )
            }
        }
    }
    // 프로필 사진 업데이트 함수
    fun updateProfileImage(imageUri: Uri) {
        profileImageUri = imageUri // 새 이미지 URI 저장
        fragmentMyPageBinding.profileImage.setImageURI(imageUri) // UI 업데이트
    }

    // 현재 프로필 이미지 URI 반환 함수
    fun getProfileImageUri(): Uri? {
        return profileImageUri
    }



    // 마이페이지 아이템 클릭시
    private fun setupMyPageOptions() {
        fragmentMyPageBinding.apply {
            myPageUserName.text = "사용자"
            myPageGroupName.text = "못난이들"
            // 밑줄 효과
            myPageGroupName.paintFlags = myPageGroupName.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            val myPageItemClickListener = View.OnClickListener { view ->
                when (view.id) {
                    // 그룹명
                    R.id.myPageGroupName -> {
                        socialActivity.replaceFragment(SettingGroupFragment())
                    }
                    // 알림
                    R.id.myPageNotificationSettingButton -> {
                        openNotificationSettings()
                    }
                    // 비밀번호 변경
                    R.id.myPageChangePasswordButton -> {

                        socialActivity.replaceFragment(ModifyUserPwFragment())
                    }
                    // 로그아웃
                    R.id.myPageLogoutButton -> {
                        myPageLogoutDialog()
                    }
                }
            }
            myPageGroupName.setOnClickListener(myPageItemClickListener)
            myPageNotificationSettingButton.setOnClickListener(myPageItemClickListener)
            myPageChangePasswordButton.setOnClickListener(myPageItemClickListener)
            myPageLogoutButton.setOnClickListener(myPageItemClickListener)
        }
    }


    // 알림창 설정화면
    private fun openNotificationSettings() {
        // Android 13 이상인지 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // POST_NOTIFICATIONS 권한 상태 확인
            when (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            )) {
                PackageManager.PERMISSION_GRANTED -> {
                    // 권한이 이미 허용된 경우 알림 설정 화면으로 이동
                    navigateToNotificationSettings()
                }

                PackageManager.PERMISSION_DENIED -> {
                    // 권한 요청
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }


    // 알림 설정 화면으로 이동하는 함수
    private fun navigateToNotificationSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${requireContext().packageName}")
            }
        }
        startActivity(intent)
    }


    // 로그아웃 다이얼로그
    private fun myPageLogoutDialog() {
        val dialog = CustomDialog(
            context = requireContext(),
            contentText = "정말 로그아웃 하시곘습니까?",
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
}