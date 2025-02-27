package com.friends.ggiriggiri.ui.fifth.questionlist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentQuestionListBinding
import com.friends.ggiriggiri.ui.adapter.QuestionListAdapter
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import com.friends.ggiriggiri.ui.fifth.questionanswer.QuestionAnswerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionListFragment : Fragment() {

    private lateinit var fragmentQuestionListBinding: FragmentQuestionListBinding
    private val viewModel: QuestionListViewModel by viewModels()
    private lateinit var adapter: QuestionListAdapter
    private lateinit var progressDialog: CustomDialogProgressbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentQuestionListBinding = FragmentQuestionListBinding.inflate(inflater, container, false)
        progressDialog = CustomDialogProgressbar(requireContext())

        settingRecyclerView()
        observeData()

        return fragmentQuestionListBinding.root
    }

    private fun settingRecyclerView() {
        adapter = QuestionListAdapter(emptyList(), this) { question ->
            Log.d("QuestionListFragment", "🔥 Item Clicked: ${question.number}")

            // 🔥 QuestionAnswerFragment 로 이동
            val socialActivity = activity as? SocialActivity
            socialActivity?.replaceFragment(QuestionAnswerFragment.newInstance(question.number.toString(), question.questionDataDocumentID))
        }

        fragmentQuestionListBinding.recyclerViewQuestionList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@QuestionListFragment.adapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
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

        viewModel.questionList.observe(viewLifecycleOwner) { list ->
            adapter.updateList(list)
        }
    }

}