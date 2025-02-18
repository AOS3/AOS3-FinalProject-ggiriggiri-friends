package com.friends.ggiriggiri.ui.third.answer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.friends.ggiriggiri.databinding.FragmentAnswerBinding
import com.github.penfeizhou.animation.apng.APNGDrawable
import com.github.penfeizhou.animation.loader.ByteBufferLoader
import dagger.hilt.android.AndroidEntryPoint
import java.net.URL
import java.nio.ByteBuffer

@AndroidEntryPoint
class AnswerFragment : Fragment() {

    private var _binding: FragmentAnswerBinding? = null
    private val binding get() = _binding!!
    private val answerViewModel: AnswerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnswerBinding.inflate(inflater, container, false)

        setupObservers()
        setupUI()

        // 뒤로가기
        binding.tbAnswer.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    // 뷰모델 관찰
    private fun setupObservers() {
        answerViewModel.questionText.observe(viewLifecycleOwner) {
            binding.tvAnswerQuestion.text = it
        }

        answerViewModel.questionImageUrl.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                loadAnimatedPng(it)
            } else {
                binding.ivAnswerEmoji.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    // Fragment에 전달된 arguments에서 질문 텍스트와 이미지 URL을 추출하여 ViewModel에 설정
    private fun setupUI() {
        arguments?.let {
            val questionText = it.getString(ARG_QUESTION_TEXT, "")
            val questionImageUrl = it.getString(ARG_QUESTION_IMAGE)

            answerViewModel.setQuestionText(questionText)
            answerViewModel.setQuestionImageUrl(questionImageUrl)
        }
    }

    private fun loadAnimatedPng(imageUrl: String) {
        val imageView = binding.ivAnswerEmoji

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
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_QUESTION_TEXT = "question_text"
        private const val ARG_QUESTION_IMAGE = "question_image"

        fun newInstance(questionText: String, questionImage: String?) =
            AnswerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_QUESTION_TEXT, questionText)
                    putString(ARG_QUESTION_IMAGE, questionImage)
                }
            }
    }
}

class CustomByteBufferLoader(private val buffer: ByteBuffer) : ByteBufferLoader() {
    override fun getByteBuffer(): ByteBuffer {
        return buffer
    }
}
