package com.friends.ggiriggiri.ui.fourth.modifyuserpw

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.R

class ModifyUserPwFragment : Fragment() {

    companion object {
        fun newInstance() = ModifyUserPwFragment()
    }

    private val viewModel: ModifyUserPwViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_modify_user_pw, container, false)
    }
}