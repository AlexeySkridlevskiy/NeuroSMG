package com.example.neurosmg.data.api

import android.content.Context
import android.content.SharedPreferences

class IdController(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context
        .getSharedPreferences(
            "user_id",
            Context.MODE_PRIVATE
        )
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveId(id: Int) {
        editor.putInt("id", id)
        editor.apply()
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt("id", -1)
    }
}