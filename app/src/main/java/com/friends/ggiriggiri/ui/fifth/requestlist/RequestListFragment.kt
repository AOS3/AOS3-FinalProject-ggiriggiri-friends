package com.friends.ggiriggiri.ui.fifth.requestlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.databinding.FragmentRequestListBinding

class RequestListFragment : Fragment() {

    lateinit var fragmentRequestListBinding: FragmentRequestListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentRequestListBinding = FragmentRequestListBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment


        return fragmentRequestListBinding.root
    }
}