package com.friends.ggiriggiri

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.friends.ggiriggiri.databinding.ActivityGroupBinding
import com.friends.ggiriggiri.ui.second.group.GroupFragment

class GroupActivity : AppCompatActivity() {

    private lateinit var activityGroupBinding: ActivityGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activityGroupBinding =ActivityGroupBinding.inflate(layoutInflater)
        setContentView(activityGroupBinding.root)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentGroupActivity, GroupFragment())
                .commit()
        }
    }
}