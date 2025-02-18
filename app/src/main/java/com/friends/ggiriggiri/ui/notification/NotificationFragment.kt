package com.friends.ggiriggiri.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    private lateinit var fragmentNotificationBinding: FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentNotificationBinding = FragmentNotificationBinding.inflate(inflater, container, false)

        // 뒤로 가기
        fragmentNotificationBinding.tbNotification.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return fragmentNotificationBinding.root
    }
}
