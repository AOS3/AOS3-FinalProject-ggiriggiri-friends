package com.friends.ggiriggiri

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.friends.ggiriggiri.databinding.ActivitySocialBinding
import com.friends.ggiriggiri.ui.fifth.SocialFragment
import com.friends.ggiriggiri.ui.fifth.memory.MemoryFragment

class SocialActivity : AppCompatActivity() {

    private lateinit var activitySocialBinding: ActivitySocialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activitySocialBinding = ActivitySocialBinding.inflate(layoutInflater)
        setContentView(activitySocialBinding.root)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewSocialMain, SocialFragment())
                .commit()
        }
    }
}