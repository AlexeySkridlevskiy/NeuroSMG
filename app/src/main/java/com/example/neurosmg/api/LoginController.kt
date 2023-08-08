package com.example.neurosmg.api

import android.content.Context
import android.content.SharedPreferences

class LoginController(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context
        .getSharedPreferences(
            "user_login",
            Context.MODE_PRIVATE
        )
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveLogin(login: String) {
        editor.putString("login", login)
        editor.apply()
    }

    fun getUserLogin(): String? {
        return sharedPreferences.getString("login", null)
    }
}