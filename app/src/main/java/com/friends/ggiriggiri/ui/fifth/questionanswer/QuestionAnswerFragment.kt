package com.friends.ggiriggiri.ui.fifth.questionanswer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentQuestionAnswerBinding
import com.friends.ggiriggiri.ui.adapter.AnswerListAdapter
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import com.friends.ggiriggiri.ui.third.home.CustomByteBufferLoader
import com.github.penfeizhou.animation.apng.APNGDrawable
import dagger.hilt.android.AndroidEntryPoint
import java.net.URL
import java.nio.ByteBuffer

@AndroidEntryPoint
class QuestionAnswerFragment : Fragment() {

    private lateinit var fragmentQuestionAnswerBinding: FragmentQuestionAnswerBinding
    private val viewModel: QuestionAnswerViewModel by viewModels()
    private lateinit var answerAdapter: AnswerListAdapter
    private lateinit var progressDialog: CustomDialogProgressbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentQuestionAnswerBinding = FragmentQuestionAnswerBinding.inflate(inflater, container, false)
        progressDialog = CustomDialogProgressbar(requireContext())

        settingToolbar()
        settingRecyclerView()
        observeData()

        val questionListDocumentID = arguments?.getString(ARG_QUESTION_LIST_ID) ?: ""
        val questionDataDocumentID = arguments?.getString(ARG_QUESTION_DATA_ID) ?: ""

        viewModel.loadQuestionData(questionListDocumentID, questionDataDocumentID)

        return fragmentQuestionAnswerBinding.root
    }

    private fun settingToolbar(){
        fragmentQuestionAnswerBinding.apply {
            tbQuestionAnswer.setTitle("")
            tbQuestionAnswer.setNavigationIcon(R.drawable.ic_arrow_back_ios)
            tbQuestionAnswer.setNavigationOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        }

        viewModel.questionData.observe(viewLifecycleOwner) { question ->
            fragmentQuestionAnswerBinding.tvQuestionAnswerContent.text = question.content

            if (question.imgUrl.isNotEmpty()) {
                loadAnimatedPng(question.imgUrl)
            } else {
                fragmentQuestionAnswerBinding.ivQuestionAnswerImogi.setImageResource(R.drawable.ic_default_profile)
            }
        }

        viewModel.answers.observe(viewLifecycleOwner) { answers ->
            Log.d("QuestionAnswerFragment", "RecyclerView 업데이트! 불러온 답변 개수: ${answers.size}")

            if (answers.isEmpty()) {
                Log.d("QuestionAnswerFragment", "불러온 답변이 없습니다.")
            } else {
                for (answer in answers) {
                    Log.d("QuestionAnswerFragment", "답변 내용: ${answer.answerMessage}, 작성자: ${answer.userName}")
                }
            }

            answerAdapter.updateList(answers)
        }
    }

    private fun settingRecyclerView(){
        answerAdapter = AnswerListAdapter(emptyList())
        fragmentQuestionAnswerBinding.recyclerViewQuestionAnswer.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = answerAdapter
        }
    }

    private fun loadAnimatedPng(imageUrl: String) {
        val imageView = fragmentQuestionAnswerBinding.ivQuestionAnswerImogi

        Thread {
            try {
                val inputStream = URL(imageUrl).openStream()
                val byteArray = inputStream.readBytes()
                val byteBuffer = ByteBuffer.wrap(byteArray)

                val apngDrawable = APNGDrawable(CustomByteBufferLoader(byteBuffer))

                if (isAdded) {
                    requireActivity().runOnUiThread {
                        imageView.setImageDrawable(apngDrawable)

                        val scale = 2.0f
                        imageView.scaleX = scale
                        imageView.scaleY = scale

                        apngDrawable.start()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        imageView.setImageResource(R.drawable.ic_default_profile)
                    }
                }
            }
        }.start()
    }

    companion object {
        private const val ARG_QUESTION_LIST_ID = "question_list_id"
        private const val ARG_QUESTION_DATA_ID = "question_data_id"

        fun newInstance(questionListDocumentID: String, questionDataDocumentID: String): QuestionAnswerFragment {
            return QuestionAnswerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_QUESTION_LIST_ID, questionListDocumentID)
                    putString(ARG_QUESTION_DATA_ID, questionDataDocumentID)
                }
            }
        }
    }
}