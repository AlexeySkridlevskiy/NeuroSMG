package com.example.neurosmg.api

import com.example.neurosmg.csvdatauploader.UploadFileResponse
import com.example.neurosmg.doctorProfile.UserResponse
import com.example.neurosmg.login.api.AuthData
import com.example.neurosmg.login.api.AuthResponse
import com.example.neurosmg.patientTestList.PatientListResponse
import com.example.neurosmg.patientTestList.addPatient.PatientRequest
import com.example.neurosmg.patientTestList.patientProfile.PatientResponse
import com.example.neurosmg.patientTestList.patientProfile.UpdatePatientRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.io.File

interface ApiService {
    @POST("/api/auth/local")
    fun login(@Body authData: AuthData): Call<AuthResponse>

    @GET("/api/users/{id}")
    fun getUserById(
        @Path("id") id: Int,
        @Header("Authorization") authorization: String
    ): Call<UserResponse>

    @GET("/api/users/{id}?populate=id_patient")
    fun getUserPatients(
        @Path("id") id: Int,
        @Header("Authorization") authorization: String
    ): Call<PatientListResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/patients")
    fun addPatient(
        @Header("Authorization") token: String,
        @Body patientRequest: PatientRequest
    ): Call<PatientResponse>

    @GET("/api/patients/{id}")
    fun getPatientById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int
    ): Call<PatientResponse>

    @PUT("/api/patients/{id}")
    fun updatePatient(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int,
        @Body updatePatientRequest: UpdatePatientRequest
    ): Call<PatientResponse>

    @Multipart
    @POST("/api/upload")
    fun uploadFile(
        @Header("Authorization") authHeader: String,
        @Part files: MultipartBody.Part
    ): Call<UploadFileResponse>

    @POST("/api/patients/{id}")
    fun getArchivePatient(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int,
        @Query("populate") populate: String = "datafiles"
    ): Call<UploadFileResponse>
}