package com.friends.ggiriggiri
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


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

