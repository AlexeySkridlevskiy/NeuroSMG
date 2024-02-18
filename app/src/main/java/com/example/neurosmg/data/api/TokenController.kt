package com.example.neurosmg.data.api

import android.content.Context
import android.content.SharedPreferences

class TokenController(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context
        .getSharedPreferences(
            "user_token",
            Context.MODE_PRIVATE
        )
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveToken(token: String) {
        editor.putString("token", token)
        editor.apply()
    }

    fun clearToken() {
        editor.putString("token", null)
        editor.apply()
    }

    fun getUserToken(): String? {
        return sharedPreferences.getString("token", null)
    }
}