package com.friends.ggiriggiri
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.friends.ggiriggiri.ui.first.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // SplashScreen API 적용 (Android 12 이상)
        val splashScreen = installSplashScreen()

        loginViewModel.validateAutoLoginToken(
            onSuccess = { user ->
                (application as App).loginUserModel = user
                navigateToGroup()
            },
            onFailure = {
                navigateToLogin()
            }
        )
    }

    private fun navigateToGroup() {
        startActivity(Intent(this, GroupActivity::class.java))
        finish()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}

