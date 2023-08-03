package com.example.neurosmg.login.api.login

import android.util.Log
import com.example.neurosmg.login.RetrofitBuilder
import com.example.neurosmg.login.api.AuthData
import com.example.neurosmg.login.api.AuthResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginController() {
    private val retrofitBuilder = RetrofitBuilder()
    private val apiService = retrofitBuilder.retrofitCreate()

    fun loginController(login: String, password: String): Boolean {
        var flag = false
        val authData = AuthData(login, password)
        Log.d("MyLog", "1")
        apiService.login(authData).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    Log.d("MyLog", "${authResponse?.jwt}")
                    Log.d("MyLog", "${authResponse?.user?.username}")
                    Log.d("MyLog", "2")
                    flag = true
                } else {
                    Log.d("MyLog", "not isSuccessful")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.d("MyLog", "Failure")
            }
        })
        Log.d("MyLog", "3")
        return flag
    }
}