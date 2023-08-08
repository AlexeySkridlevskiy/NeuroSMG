package com.example.neurosmg.api

import com.example.neurosmg.login.api.AuthData
import com.example.neurosmg.login.api.AuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.*

interface ApiService {
    @POST("/api/auth/local")
    fun login(@Body authData: AuthData): Call<AuthResponse>
}