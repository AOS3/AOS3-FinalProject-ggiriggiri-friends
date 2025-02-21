package com.friends.ggiriggiri
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.friends.ggiriggiri.LoginActivity
import com.friends.ggiriggiri.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // SplashScreen API 적용 (Android 12 이상)
        val splashScreen = installSplashScreen()

        // 일정 시간 후 LoginActivity로 이동
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}

