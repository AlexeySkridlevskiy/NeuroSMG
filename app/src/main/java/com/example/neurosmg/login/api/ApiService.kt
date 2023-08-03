package com.example.neurosmg.login.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.*

interface ApiService {
    @POST("/api/auth/local")
    fun login(@Body authData: AuthData): Call<AuthResponse>
}