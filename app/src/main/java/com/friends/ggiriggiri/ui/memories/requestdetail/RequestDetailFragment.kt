package com.friends.ggiriggiri.ui.memories.requestdetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentRequestDetailBinding
import com.friends.ggiriggiri.ui.adapter.ResponseListAdapter
import com.friends.ggiriggiri.ui.custom.CustomDialogProgressbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class RequestDetailFragment : Fragment() {

    private lateinit var fragmentRequestDetailBinding: FragmentRequestDetailBinding
    private val viewModel: RequestDetailViewModel by viewModels()
    private lateinit var responseAdapter: ResponseListAdapter
    private lateinit var progressDialog: CustomDialogProgressbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentRequestDetailBinding = FragmentRequestDetailBinding.inflate(layoutInflater)
        progressDialog = CustomDialogProgressbar(requireContext())

        settingToolbar()
        settingRecyclerView()
        val requestId = arguments?.getString("requestId") ?: return fragmentRequestDetailBinding.root
        Log.d("RequestDetailFragment", "🔥 RequestId 수신: $requestId")
        viewModel.loadRequestDetail(requestId)
        observeData()

        return fragmentRequestDetailBinding.root
    }

    // 툴바 세팅
    private fun settingToolbar(){
        fragmentRequestDetailBinding.apply {
            tbRequestDetail.setNavigationIcon(R.drawable.ic_arrow_back_ios)
            tbRequestDetail.setNavigationOnClickListener {
                parentFragmentManager.popBackStackImmediate()
            }
        }
    }

    private fun settingRecyclerView() {
        responseAdapter = ResponseListAdapter(emptyList(), emptyMap())
        fragmentRequestDetailBinding.recyclerViewRequestDetail.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = responseAdapter
        }
    }

    private fun observeData() {

        // 프로그레스 바
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        }

        viewModel.requestDetail.observe(viewLifecycleOwner) { request ->
            request?.let {
                fragmentRequestDetailBinding.tvRequestDetailUserName.text = it.requestUserDocumentID
                fragmentRequestDetailBinding.tvRequestDetailRequestTime.text = formatDate(it.requestTime)
                fragmentRequestDetailBinding.tvRequestDetailMessage.text = it.requestMessage

                Glide.with(this)
                    .load(it.requestUserProfileImage)
                    .placeholder(R.drawable.ic_default_profile)
                    .circleCrop()
                    .into(fragmentRequestDetailBinding.ivRequestDetailProfile)

                if (it.requestImage.isNotEmpty()) {
                    Glide.with(this)
                        .load(it.requestImage)
                        .into(fragmentRequestDetailBinding.ivRequestDetailMessage)
                }

                responseAdapter.updateList(it.responseList, emptyMap())
            }
        }

        viewModel.userProfileImageMap.observe(viewLifecycleOwner) { profileImageMap ->
            responseAdapter.updateList(responseAdapter.responseList, profileImageMap)
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    companion object {
        @JvmStatic
        fun newInstance(requestId: String) = RequestDetailFragment().apply {
            arguments = Bundle().apply {
                putString("requestId",requestId)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentRequestDetailBinding.ivRequestDetailMessage.viewTreeObserver.addOnGlobalLayoutListener {
            val width = fragmentRequestDetailBinding.ivRequestDetailMessage.width
            fragmentRequestDetailBinding.ivRequestDetailMessage.layoutParams.height = width
            fragmentRequestDetailBinding.ivRequestDetailMessage.requestLayout()
        }
    }
}