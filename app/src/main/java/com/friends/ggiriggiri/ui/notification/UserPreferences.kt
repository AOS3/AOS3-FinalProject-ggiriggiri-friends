package com.friends.ggiriggiri.ui.notification

import android.content.Context
import android.content.SharedPreferences

object UserPreferences {

    private const val PREF_NAME = "user_notification_prefs"
    private const val KEY_USER_DOCUMENT_ID = "user_id"

    fun saveUserID(context: Context, userID: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_USER_DOCUMENT_ID, userID).apply()
    }

    fun getUserID(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_DOCUMENT_ID, null)
    }

    fun clearUserID(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(KEY_USER_DOCUMENT_ID).apply()
    }
}
