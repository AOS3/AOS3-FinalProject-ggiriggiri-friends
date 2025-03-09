package com.friends.ggiriggiri

import android.app.Application
import android.content.Context
import android.util.Log
import com.friends.ggiriggiri.ui.start.register.UserModel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){
    lateinit var loginUserModel : UserModel
    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // 앱 실행될 때마다 IS_APP_RESTARTED = true로 설정하고 즉시 반영
        sharedPreferences.edit().putBoolean("IS_APP_RESTARTED", true).commit() // 동기적으로 저장

        // 저장 후 다시 값을 읽어서 확인 (디버깅용)
        val checkValue = sharedPreferences.getBoolean("IS_APP_RESTARTED", false)
        Log.d("testApp", "앱 실행 후 IS_APP_RESTARTED 값 확인: $checkValue")
    }
}