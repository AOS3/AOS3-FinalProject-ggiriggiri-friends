package com.friends.ggiriggiri.ui.fourth.modifygroupname

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.R
import com.friends.ggiriggiri.SocialActivity
import com.friends.ggiriggiri.databinding.FragmentModifyGroupNameBinding

class ModifyGroupNameFragment : Fragment() {

    private val viewModel: ModifyGroupNameViewModel by viewModels()
    lateinit var fragmentModifyGroupNameBinding: FragmentModifyGroupNameBinding
    lateinit var socialActivity: SocialActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentModifyGroupNameBinding = FragmentModifyGroupNameBinding.inflate(inflater)
        socialActivity = activity as SocialActivity

        return inflater.inflate(R.layout.fragment_modify_group_name, container, false)


    }
}