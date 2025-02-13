package com.friends.ggiriggiri

import android.os.Bundle
import android.text.TextUtils.replace
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.friends.ggiriggiri.ui.fourth.modifygroupname.ModifyGroupNameFragment
import com.friends.ggiriggiri.ui.fourth.modifygrouppw.ModifyGroupPwFragment
import com.friends.ggiriggiri.ui.fourth.modifyuserpw.ModifyUserPwFragment
import com.friends.ggiriggiri.ui.fourth.mypage.MyPageFragment
import com.friends.ggiriggiri.ui.fourth.settinggroup.SettingGroupFragment
import com.google.android.material.transition.MaterialSharedAxis

class SocialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

