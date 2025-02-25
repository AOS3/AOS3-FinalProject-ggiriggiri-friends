package com.friends.ggiriggiri.ui.third.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.databinding.FragmentProfileBottomSheetBinding
import com.friends.ggiriggiri.ui.adapter.ProfileAdapter
import com.friends.ggiriggiri.ui.adapter.ProfileItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentProfileBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var profileAdapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBottomSheetBinding.inflate(inflater, container, false)

        setupRecyclerView()
        loadUserProfiles()
        observeUserProfiles()

        return binding.root
    }

    private fun setupRecyclerView() {
        profileAdapter = ProfileAdapter(emptyList()) // 초기 빈 리스트 설정
        binding.rvProfileList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProfileList.adapter = profileAdapter
    }

    private fun loadUserProfiles() {
        val loginUser = (requireActivity().application as App).loginUserModel
        val userGroupId = loginUser.userGroupDocumentID

        homeViewModel.loadGroupUserProfiles(userGroupId)
    }

    private fun observeUserProfiles() {

        homeViewModel.userProfiles.observe(viewLifecycleOwner) { profiles ->

            if (profiles.isNotEmpty()) {
                val profileItems = profiles.map { ProfileItem(it.second, it.first) }
                profileAdapter.updateData(profileItems)
            } else {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): ProfileBottomSheetFragment {
            return ProfileBottomSheetFragment()
        }
    }
}
