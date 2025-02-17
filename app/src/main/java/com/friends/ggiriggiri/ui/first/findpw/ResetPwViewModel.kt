package com.friends.ggiriggiri.ui.first.findpw

import androidx.lifecycle.ViewModel
import com.friends.ggiriggiri.ui.first.register.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResetPwViewModel @Inject constructor(
    private val service: UserService
) : ViewModel() {

}