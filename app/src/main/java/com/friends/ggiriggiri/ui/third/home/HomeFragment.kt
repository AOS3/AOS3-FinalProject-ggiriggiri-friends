package com.friends.ggiriggiri.ui.third.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    lateinit var fragmentHomeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater,container,false)

        return fragmentHomeBinding.root
    }

}