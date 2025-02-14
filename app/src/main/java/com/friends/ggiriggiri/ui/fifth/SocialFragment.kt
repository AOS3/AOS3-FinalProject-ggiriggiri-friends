package com.friends.ggiriggiri.ui.fifth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.databinding.FragmentSocialBinding
import com.friends.ggiriggiri.ui.fifth.memory.MemoryFragment

class SocialFragment : Fragment() {

    private lateinit var fragmentSocialBinding: FragmentSocialBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentSocialBinding = FragmentSocialBinding.inflate(layoutInflater)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentSocialSub, MemoryFragment())
                .commit()
        }

        return fragmentSocialBinding.root
    }

}