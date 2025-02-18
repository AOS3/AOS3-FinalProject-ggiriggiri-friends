package com.friends.ggiriggiri.ui.third.request

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentRequestBinding

class RequestFragment : Fragment() {

    private lateinit var fragmentRequestBinding: FragmentRequestBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRequestBinding = FragmentRequestBinding.inflate(inflater, container, false)

        // 뒤로가기
        fragmentRequestBinding.tbRequest.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return fragmentRequestBinding.root
    }

}