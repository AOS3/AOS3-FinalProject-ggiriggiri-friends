package com.friends.ggiriggiri.ui.third.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import java.net.URL
import java.nio.ByteBuffer

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var socialActivity: SocialActivity
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        socialActivity = activity as SocialActivity

        val loginUser = (requireActivity().application as App).loginUserModel
        val userGroupId = loginUser.userGroupDocumentID

        homeViewModel.loadActiveRequests(userGroupId)

        setupObservers()
        setupClickListeners()

        return binding.root
    }

    private fun setupObservers() {
        homeViewModel.todayQuestionList.observe(viewLifecycleOwner) { questionList ->
            if (questionList == null) return@observe

            // 카드뷰 배경색 적용
            try {
                binding.cvHomeQuestion.setCardBackgroundColor(Color.parseColor(questionList.color))
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                binding.cvHomeQuestion.setCardBackgroundColor(Color.parseColor("#FFFFFF")) // 기본 색상
            }

            // 질문 텍스트 적용
            binding.tvHomeQuestionContent.text = questionList.content

            // 움직이는 이모지 적용
            if (!questionList.imgUrl.isNullOrEmpty()) {
                loadAnimatedPng(questionList.imgUrl)
            } else {
                binding.ivHomeQuestionEmoji.setImageResource(R.drawable.ic_image)
            }
        }

        // 🔥 활성화된 요청 표시
        homeViewModel.activeRequests.observe(viewLifecycleOwner) { requestList ->
            if (requestList.isNotEmpty()) {
                val activeRequest = requestList[0]

                // 요청 활성화 상태 UI 적용
                binding.tvHomeRequestContent.text = activeRequest.requestMessage
                binding.ivHomeRequestStatus.setColorFilter(Color.parseColor("#4CAF50")) // 활성화 상태
                binding.btnHomeRespond.text = "응답하기"
                binding.btnHomeRespond.setOnClickListener {
                    socialActivity.replaceFragment(ResponseFragment()) // 응답하기 화면으로 이동
                }
            } else {
                // 요청 없음 UI 적용
                binding.tvHomeRequestContent.text = "요청이 비어있어요"
                binding.ivHomeRequestStatus.setColorFilter(Color.parseColor("#858282")) // 비활성화 상태
                binding.btnHomeRespond.text = "요청하기"
                binding.btnHomeRespond.setOnClickListener {
                    socialActivity.replaceFragment(RequestFragment()) // 요청하기 화면으로 이동
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnHomeAnswer.setOnClickListener {
            val questionContent = binding.tvHomeQuestionContent.text.toString()
            val questionImageUrl = homeViewModel.todayQuestionList.value?.imgUrl

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
        _binding = null
    }
}

class CustomByteBufferLoader(private val buffer: ByteBuffer) : ByteBufferLoader() {
    override fun getByteBuffer(): ByteBuffer {
        return buffer
    }
}
