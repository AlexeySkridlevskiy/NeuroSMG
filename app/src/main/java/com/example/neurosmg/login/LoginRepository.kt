package com.example.neurosmg.login

import android.util.Log
import android.widget.Toast
import com.example.neurosmg.login.api.ApiService
import com.example.neurosmg.login.api.AuthData
import com.example.neurosmg.login.api.AuthResponse
import com.example.neurosmg.login.api.login.LoginController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginRepository() {
    private val loginController = LoginController()
    fun login(
        login: String,
        password: String
    ): Boolean {
        val flag = loginController.loginController(login, password)
        Log.d("MyLog", "Flag login = $flag")
        return true
    }
}