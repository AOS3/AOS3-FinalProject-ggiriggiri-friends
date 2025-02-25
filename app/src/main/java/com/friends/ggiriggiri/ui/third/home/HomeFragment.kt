package com.friends.ggiriggiri.ui.third.home

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentHomeBinding
import com.friends.ggiriggiri.ui.notification.NotificationFragment
import com.friends.ggiriggiri.ui.third.answer.AnswerFragment
import com.friends.ggiriggiri.ui.third.request.RequestFragment
import com.friends.ggiriggiri.ui.third.response.ResponseFragment
import com.github.penfeizhou.animation.apng.APNGDrawable
import com.github.penfeizhou.animation.loader.ByteBufferLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.URL
import java.nio.ByteBuffer

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var socialActivity: SocialActivity
    private val homeViewModel: HomeViewModel by viewModels()
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        socialActivity = activity as SocialActivity

        val loginUser = (requireActivity().application as App).loginUserModel
        val userGroupId = loginUser.userGroupDocumentID

        homeViewModel.loadLatestRequest(userGroupId)
        homeViewModel.loadGroupName(userGroupId)
        homeViewModel.loadGroupUserProfiles(userGroupId)
        homeViewModel.loadTodayQuestion(userGroupId)

        setupObservers()
        setupClickListeners()

        return binding.root
    }

    private fun setupObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.question.collectLatest { question ->
                if (question == null) return@collectLatest

                binding?.let { safeBinding ->
                    val safeColor = question.questionColor ?: "#FFFFFF"
                    try {
                        safeBinding.cvHomeQuestion.setCardBackgroundColor(Color.parseColor(safeColor))
                    } catch (e: IllegalArgumentException) {
                        safeBinding.cvHomeQuestion.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                    }

                    safeBinding.tvHomeQuestionContent.text = question.questionContent

                    if (!question.questionImg.isNullOrEmpty()) {
                        loadAnimatedPng(question.questionImg)
                    } else {
                        safeBinding.ivHomeQuestionEmoji.setImageResource(R.drawable.ic_image)
                    }
                }
            }
        }


        homeViewModel.groupName.observe(viewLifecycleOwner) { groupName ->
            binding.tbHome.title = groupName
        }

        homeViewModel.userProfiles.observe(viewLifecycleOwner) { profiles ->
            val profileViews = listOf(
                binding.ivHomeProfile1,
                binding.ivHomeProfile2,
                binding.ivHomeProfile3,
                binding.ivHomeProfile4
            )

            // 🔥 모든 프로필 뷰를 기본적으로 숨김
            profileViews.forEach { it.visibility = View.GONE }

            // 🔥 프로필 리스트 개수만큼만 뷰를 보여주고, 이미지 설정
            profiles.take(4).forEachIndexed { index, profile ->
                profileViews[index].visibility = View.VISIBLE // ✅ 필요한 프로필만 표시

                val imageUrl = profile.second
                if (imageUrl.isNullOrEmpty()) {
                    profileViews[index].setImageResource(R.drawable.ic_default_profile) // 기본 프로필 적용
                } else {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_default_profile) // 로딩 중 기본 프로필
                        .error(R.drawable.ic_default_profile) // 오류 시 기본 프로필
                        .into(profileViews[index])
                }
            }
        }


        // 활성화된 요청 표시
        homeViewModel.latestRequest.observe(viewLifecycleOwner) { latestRequest ->
            countDownTimer?.cancel() // 기존 타이머 중지

            if (latestRequest == null) {
                Log.d("HomeFragment", "최신 요청 없음 → 요청하기 버튼 표시")
                binding.tvHomeRequestContent.text = "요청이 없습니다!"
                binding.ivHomeRequestStatus.setColorFilter(Color.parseColor("#858282")) // 비활성화 상태
                binding.btnHomeRespond.text = "요청하기"
                binding.btnHomeRespond.isEnabled = true
                binding.btnHomeRespond.setOnClickListener {
                    Log.d("HomeFragment", "요청하기 화면으로 이동")
                    socialActivity.replaceFragment(RequestFragment())
                }
                return@observe
            }

            val loginUser = (requireActivity().application as App).loginUserModel
            val userId = loginUser.userDocumentId
            val isRequester = latestRequest.requestUserDocumentID == userId
            val requestTime = latestRequest.requestTime ?: System.currentTimeMillis()
            val elapsedTime = System.currentTimeMillis() - requestTime
            val elapsedMinutes = elapsedTime / 1000 / 60
            val elapsedSeconds = (elapsedTime / 1000) % 60

            Log.d("HomeFragment", "최신 요청 ID: ${latestRequest.requestId}, 요청된 지 ${elapsedMinutes}분 ${elapsedSeconds}초 지남")

            homeViewModel.checkUserResponseExists(latestRequest.requestId, userId) { hasUserResponded ->
                requireActivity().runOnUiThread {
                    binding.tvHomeRequestContent.text = latestRequest.requestMessage

                    val requestState = latestRequest.requestState
                    Log.d("HomeFragment", "현재 요청 상태: $requestState")

                    when (requestState) {
                        1 -> { // 요청 활성화 (응답 가능)
                            Log.d("HomeFragment", "요청 활성화 상태")

                            binding.ivHomeRequestStatus.setColorFilter(Color.parseColor("#4CAF50")) // 활성화 상태
                            val requestEndTime = requestTime + (30 * 60 * 1000) // 30분 후 요청 마감
                            val now = System.currentTimeMillis()
                            val timeRemaining = requestEndTime - now

                            if (hasUserResponded || isRequester) {
                                // 응답한 사용자이거나 요청자인 경우 → 응답보기 버튼 설정
                                Log.d("HomeFragment", "응답한 사용자 또는 요청자 → 응답보기 버튼 설정")
                                binding.btnHomeRespond.text = "응답보기"
                                binding.btnHomeRespond.isEnabled = true
                                binding.btnHomeRespond.setOnClickListener {
                                    val bundle = Bundle().apply {
                                        putString("requestId", latestRequest.requestId)
                                    }
                                    val responseDetailFragment = AnswerFragment().apply {
                                        arguments = bundle
                                    }
                                    Log.d("HomeFragment", "응답보기 화면으로 이동: ${latestRequest.requestId}")
                                    socialActivity.replaceFragment(responseDetailFragment)
                                }
                            } else {
                                // 응답하지 않은 경우 → 응답하기 버튼 설정
                                Log.d("HomeFragment", "응답하지 않은 사용자 → 응답하기 버튼 설정")

                                if (timeRemaining > 0) {
                                    countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
                                        override fun onTick(millisUntilFinished: Long) {
                                            val minutes = millisUntilFinished / 1000 / 60
                                            val seconds = (millisUntilFinished / 1000) % 60
                                            binding.btnHomeRespond.text = "응답하기\n(${String.format("%02d:%02d", minutes, seconds)})"
                                        }

                                        override fun onFinish() {
                                            Log.d("HomeFragment", "응답 마감됨 → 요청 대기 상태로 변경")
                                            binding.ivHomeRequestStatus.setColorFilter(Color.parseColor("#858282"))
                                            binding.btnHomeRespond.text = "응답 마감됨"
                                            binding.btnHomeRespond.isEnabled = false
                                        }
                                    }.start()
                                } else {
                                    binding.btnHomeRespond.text = "응답 마감됨"
                                    binding.btnHomeRespond.isEnabled = false
                                }

                                binding.btnHomeRespond.setOnClickListener {
                                    val bundle = Bundle().apply {
                                        putString("requestId", latestRequest.requestId)
                                        putString("requestUserDocumentId", latestRequest.requestUserDocumentID)
                                        putString("requestMessage", latestRequest.requestMessage)
                                    }
                                    val responseFragment = ResponseFragment().apply {
                                        arguments = bundle
                                    }
                                    Log.d("HomeFragment", "응답하기 화면으로 이동: ${latestRequest.requestId}")
                                    socialActivity.replaceFragment(responseFragment)
                                }
                            }
                        }

                        2 -> { // 요청 마감 (30분 경과 후)
                            Log.d("HomeFragment", "응답 마감됨 → 요청 대기 상태")

                            binding.ivHomeRequestStatus.setColorFilter(Color.parseColor("#858282")) // 마감 상태
                            val nextRequestTime = requestTime + (60 * 60 * 1000) // 1시간 후 요청 가능
                            val now = System.currentTimeMillis()
                            val cooldownTimeRemaining = nextRequestTime - now

                            if (cooldownTimeRemaining > 0) {
                                countDownTimer = object : CountDownTimer(cooldownTimeRemaining, 1000) {
                                    override fun onTick(millisUntilFinished: Long) {
                                        val minutes = millisUntilFinished / 1000 / 60
                                        val seconds = (millisUntilFinished / 1000) % 60
                                        binding.tvHomeRequestContent.text = "다음 요청을 기다려주세요"
                                        binding.btnHomeRespond.text = "요청하기\n(${String.format("%02d:%02d", minutes, seconds)})"
                                        binding.btnHomeRespond.isEnabled = false
                                    }

                                    override fun onFinish() {
                                        binding.ivHomeRequestStatus.setColorFilter(Color.parseColor("#858282"))
                                        binding.btnHomeRespond.text = "요청하기"
                                        binding.tvHomeRequestContent.text = "요청이 없습니다!!"
                                        binding.btnHomeRespond.isEnabled = true
                                        binding.btnHomeRespond.setOnClickListener {
                                            Log.d("HomeFragment", "요청하기 화면으로 이동")
                                            socialActivity.replaceFragment(RequestFragment())
                                        }
                                    }
                                }.start()
                            } else {
                                binding.ivHomeRequestStatus.setColorFilter(Color.parseColor("#858282"))
                                binding.btnHomeRespond.text = "요청하기"
                                binding.tvHomeRequestContent.text = "요청이 없습니다!!"
                                binding.btnHomeRespond.isEnabled = true
                                binding.btnHomeRespond.setOnClickListener {
                                    socialActivity.replaceFragment(RequestFragment())
                                }
                            }
                        }

                        3 -> { // 요청 가능 상태 (1시간 후 다시 요청 가능)
                            Log.d("HomeFragment", "요청 가능 상태 → 요청하기 버튼 표시")

                            binding.ivHomeRequestStatus.setColorFilter(Color.parseColor("#858282")) // 기본 상태
                            binding.btnHomeRespond.text = "요청하기"
                            binding.tvHomeRequestContent.text = "요청이 없습니다!!!"
                            binding.btnHomeRespond.isEnabled = true
                            binding.btnHomeRespond.setOnClickListener {
                                Log.d("HomeFragment", "요청하기 화면으로 이동")
                                socialActivity.replaceFragment(RequestFragment())
                            }
                        }

                        else -> {
                            Log.w("HomeFragment", "알 수 없는 상태 → 기본 상태 적용")
                            binding.ivHomeRequestStatus.setColorFilter(Color.parseColor("#858282"))
                            binding.btnHomeRespond.text = "요청하기"
                            binding.tvHomeRequestContent.text = " "
                            binding.btnHomeRespond.isEnabled = true
                            binding.btnHomeRespond.setOnClickListener {
                                socialActivity.replaceFragment(RequestFragment())
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnHomeAnswer.setOnClickListener {
            val questionContent = binding.tvHomeQuestionContent.text.toString()
            val questionImageUrl = homeViewModel.question.value?.questionImg

            val fragment = AnswerFragment.newInstance(questionContent, questionImageUrl)
            socialActivity.replaceFragment(fragment)
        }


        binding.tvHomeProfileSeeAll.setOnClickListener {
            val bottomSheet = ProfileBottomSheetFragment()
            bottomSheet.show(parentFragmentManager, "ProfileBottomSheet")
        }

        binding.tbHome.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_notification -> {
                    socialActivity.replaceFragment(NotificationFragment())
                    true
                }
                else -> false
            }
        }

        binding.tvHomeProfileSeeAll.setOnClickListener {
            val loginUser = (requireActivity().application as App).loginUserModel
            val userGroupId = loginUser.userGroupDocumentID

            Log.d("100HomeFragment", "🔍 전체보기 버튼 클릭됨 - userGroupId: $userGroupId")

            val bottomSheet = ProfileBottomSheetFragment.newInstance()

            bottomSheet.show(parentFragmentManager, "ProfileBottomSheet")
        }
    }

    private fun loadAnimatedPng(imageUrl: String) {
        val imageView = binding.ivHomeQuestionEmoji

        Thread {
            try {
                val inputStream = URL(imageUrl).openStream()
                val byteArray = inputStream.readBytes()
                val byteBuffer = ByteBuffer.wrap(byteArray)

                val apngDrawable = APNGDrawable(CustomByteBufferLoader(byteBuffer))

                if (isAdded) {
                    requireActivity().runOnUiThread {
                        imageView.setImageDrawable(apngDrawable)
                        apngDrawable.start()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        binding.ivHomeQuestionEmoji.setImageResource(R.drawable.ic_image)
                    }
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}

class CustomByteBufferLoader(private val buffer: ByteBuffer) : ByteBufferLoader() {
    override fun getByteBuffer(): ByteBuffer {
        return buffer
    }
}
