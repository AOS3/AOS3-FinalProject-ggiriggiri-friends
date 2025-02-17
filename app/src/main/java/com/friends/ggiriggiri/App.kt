package com.friends.ggiriggiri

import android.app.Application
import com.friends.ggiriggiri.ui.first.register.UserModel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){
    lateinit var loginUserModel : UserModel
}