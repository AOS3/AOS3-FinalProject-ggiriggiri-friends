package com.friends.ggiriggiri.ui.fourth.mypage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentMyPageBinding
import com.friends.ggiriggiri.ui.custom.CustomDialog
import com.friends.ggiriggiri.ui.fourth.modifyuserpw.ModifyUserPwFragment
import com.friends.ggiriggiri.ui.fourth.settinggroup.SettingGroupFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class MyPageFragment : Fragment() {

    private lateinit var fragmentMyPageBinding: FragmentMyPageBinding
    private lateinit var socialActivity: SocialActivity

    private val myPageViewModel: MyPageViewModel by viewModels()

    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ActivityResultLauncher 초기화
        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // 권한이 허용되었을 때
//                navigateToNotificationSettings()
                openNotificationSettings()
            } else {
                // 권한이 거부되었을 때
//                openNotificationSettings()
                navigateToNotificationSettings()
               Toast.makeText(requireContext(), "권한이 거부되었습니다..", Toast.LENGTH_SHORT).show()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginUser = (requireActivity().application as App).loginUserModel
        myPageViewModel.userDocumentId = loginUser.userDocumentId
        myPageViewModel.gettingUserAndGroupName()


        // 소셜 로그인 여부 확인
        myPageViewModel.checkSocialLogin()

        // 비밀번호 변경 항목 가시성 제어
        myPageViewModel.isSocialLogin.observe(viewLifecycleOwner) { isSocial ->
            fragmentMyPageBinding.myPageChangePasswordButton.visibility =
                if (isSocial) View.GONE else View.VISIBLE
        }

        setupObservers()
    }

    // Toolbar
    private fun settingToolbar() {
        fragmentMyPageBinding.apply {
            toolbarMyPage.setTitle("마이페이지")
        }
    }

    // LiveData 관찰
    private fun setupObservers() {
        // 프로필 이미지 URL 관찰 및 UI 업데이트
        myPageViewModel.profileImageUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.ic_default_profile)
                    .into(fragmentMyPageBinding.profileImage)
            } else {
                fragmentMyPageBinding.profileImage.setImageResource(R.drawable.ic_default_profile)
            }
        }
        // 사용자 이름
        myPageViewModel.userName.observe(viewLifecycleOwner) { name ->
            fragmentMyPageBinding.myPageUserName.text = name
        }
        // 그룹명
        myPageViewModel.groupName.observe(viewLifecycleOwner) { group ->
            fragmentMyPageBinding.myPageGroupName.text = group
        }
    }

    fun getSecureProfileImageUri(): Uri? {
        val originalUri = myPageViewModel.profileImageUri.value ?: return null

        // 만약 URI의 스킴이 http/https 인 경우 그대로 사용
        val scheme = originalUri.scheme?.lowercase()
        if (scheme == "https" || scheme == "http") {
            return originalUri
        }

        // 스킴이 content인 경우 파일을 복사해서 안전한 URI 생성
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(originalUri)
            val picturesDir = requireContext().getExternalFilesDir("Pictures")
            if (picturesDir == null) {
                Log.e("MyPageFragment", "Pictures directory not found!")
                null
            } else {
                val destFile = File(picturesDir, "profile_temp.jpg")
                FileOutputStream(destFile).use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
                inputStream?.close()
                FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    destFile
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MyPageFragment", "Error creating secure URI: ${e.message}")
            null
        }
    }

    // Profile 클릭시
    private fun settingProfile() {
        fragmentMyPageBinding.apply {
            profileImage.setOnClickListener {
                val myPageBottomSheetFragment = MyPageBottomSheetFragment()
                myPageBottomSheetFragment.show(
                    childFragmentManager, "MyPageBottomSheet"
                )
            }
        }
    }

    // MyPageBottomSheetFragment나 다른 이미지 선택 UI가 호출할 함수
    fun updateProfileImage(selectedImageUri: Uri) {
        // 선택한 이미지의 Uri를 문자열로 변환해 ViewModel에 전달하면
        // Firestore의 userProfileImage 필드가 업데이트됩니다.
        myPageViewModel.updateProfileImage(selectedImageUri.toString())
        Toast.makeText(requireContext(), "프로필 이미지가 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
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
//                    navigateToNotificationSettings()
                    Toast.makeText(requireContext(), "권한이 허용되었습니다..", Toast.LENGTH_SHORT).show()
                }

                PackageManager.PERMISSION_DENIED -> {
                    // 권한 요청
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    navigateToNotificationSettings()
                }
            }
        }
    }


    // 알림 설정 화면으로 이동하는 함수
    private fun navigateToNotificationSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, activity?.packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK

            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${activity?.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
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
                // Firebase를 사용하는 경우 로그아웃 처리 예제
                FirebaseAuth.getInstance().signOut()

                // 기존 액티비티 스택을 모두 제거하고 LoginActivity로 전환
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            },
            negativeText = "아니오",
            onNegativeClick = {

            }
        )
        dialog.showCustomDialog()
    }
}

