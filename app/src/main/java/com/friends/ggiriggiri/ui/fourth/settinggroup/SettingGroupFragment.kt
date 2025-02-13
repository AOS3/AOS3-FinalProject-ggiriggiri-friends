package com.friends.ggiriggiri.ui.fourth.settinggroup

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friends.ggiriggiri.R

class SettingGroupFragment : Fragment() {

    companion object {
        fun newInstance() = SettingGroupFragment()
    }

    private val viewModel: SettingGroupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_setting_group, container, false)
    }
}