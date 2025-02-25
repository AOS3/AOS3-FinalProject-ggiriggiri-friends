package com.friends.ggiriggiri.ui.fifth.requestlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.friends.ggiriggiri.data.model.RequestModel
import com.friends.ggiriggiri.databinding.FragmentRequestListBinding
import com.friends.ggiriggiri.ui.adapter.RequestListAdapter
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestListFragment : Fragment() {

    lateinit var fragmentRequestListBinding: FragmentRequestListBinding
    private lateinit var adapter : RequestListAdapter
    private val viewModel: RequestListViewModel by viewModels()
    private lateinit var progressDialog: CustomDialogProgressbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentRequestListBinding = FragmentRequestListBinding.inflate(layoutInflater)
        progressDialog = CustomDialogProgressbar(requireContext())

        settingRecyclerView()
        observeData()


        return fragmentRequestListBinding.root
    }


    // 리사이클러뷰
    private fun settingRecyclerView() {
        adapter = RequestListAdapter(emptyList(), this)
        fragmentRequestListBinding.recyclerViewRequsetList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RequestListFragment.adapter
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
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

        viewModel.requestList.observe(viewLifecycleOwner) { list ->
            adapter.updateList(list)
        }
    }
}