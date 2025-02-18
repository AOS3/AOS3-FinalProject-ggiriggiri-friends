package com.friends.ggiriggiri.ui.third.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentProfileBottomSheetBinding
import com.friends.ggiriggiri.ui.adapter.ProfileAdapter
import com.friends.ggiriggiri.ui.adapter.ProfileItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentProfileBottomSheetBinding
    private val profileList = listOf(
        ProfileItem(R.drawable.ic_default_profile, "맑은 사과"),
        ProfileItem(R.drawable.ic_default_profile, "하얀늑대"),
        ProfileItem(R.drawable.ic_default_profile, "아아 중독자"),
        ProfileItem(R.drawable.ic_default_profile, "썩은 사과"),
        ProfileItem(R.drawable.ic_default_profile, "빨간늑대"),
        ProfileItem(R.drawable.ic_default_profile, "뜨아 중독자"),
        ProfileItem(R.drawable.ic_default_profile, "가나디")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBottomSheetBinding.inflate(inflater, container, false)

        // 📌 RecyclerView 설정 (2x2 그리드)
        binding.rvProfileList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProfileList.adapter = ProfileAdapter(profileList)

        return binding.root
    }
}
