package com.example.neurosmg.api

import com.example.neurosmg.doctorProfile.UserResponse
import com.example.neurosmg.login.api.AuthData
import com.example.neurosmg.login.api.AuthResponse
import com.example.neurosmg.patientTestList.PatientListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.*

interface ApiService {
    @POST("/api/auth/local")
    fun login(@Body authData: AuthData): Call<AuthResponse>

    @GET("/api/users/{id}")
    fun getUserById(@Path("id") id: Int, @Header("Authorization") authorization: String): Call<UserResponse>
    @GET("/api/users/{id}?populate=id_patient")
    fun getUserPatients(@Path("id") id: Int, @Header("Authorization") authorization: String): Call<PatientListResponse>
}