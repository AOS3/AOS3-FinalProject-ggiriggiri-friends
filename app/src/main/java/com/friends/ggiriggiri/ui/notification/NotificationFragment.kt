package com.friends.ggiriggiri.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import com.friends.ggiriggiri.databinding.FragmentNotificationBinding
import com.friends.ggiriggiri.App

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private lateinit var fragmentNotificationBinding: FragmentNotificationBinding
    private val notificationViewModel: NotificationViewModel by viewModels()
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentNotificationBinding = FragmentNotificationBinding.inflate(inflater, container, false)

        // 뒤로가기 버튼
        fragmentNotificationBinding.tbNotification.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val loginUser = (requireActivity().application as App).loginUserModel
        notificationViewModel.selectUserNotification(loginUser.userDocumentId)

        settingRecyclerView()

        return fragmentNotificationBinding.root
    }

    private fun settingRecyclerView() {
        notificationAdapter = NotificationAdapter()
        fragmentNotificationBinding.rvNotification.apply {
            adapter = notificationAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.HORIZONTAL))
                //addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
        notificationViewModel.userNotifications.observe(viewLifecycleOwner) { notifications ->
            notificationAdapter.submitList(notifications)
        }
    }
}
