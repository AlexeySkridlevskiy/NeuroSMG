package com.example.neurosmg.login

import android.content.Context
import android.content.SharedPreferences

class UserSessionManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context
        .getSharedPreferences(
            "user_session",
            Context.MODE_PRIVATE
        )
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveUserLoggedIn(isLoggedIn: Boolean) {
        editor.putBoolean("is_user_logged_in", isLoggedIn)
        editor.apply()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_user_logged_in", false)
    }
}