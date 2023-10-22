package com.example.neurosmg.api

import FileData
import com.example.neurosmg.csvdatauploader.RequestSendIdFile
import com.example.neurosmg.csvdatauploader.ResponseSendIds
import com.example.neurosmg.csvdatauploader.UploadFileResponse
import com.example.neurosmg.doctorProfile.UserResponse
import com.example.neurosmg.login.api.AuthData
import com.example.neurosmg.login.api.AuthResponse
import com.example.neurosmg.patientTestList.PatientListResponse
import com.example.neurosmg.patientTestList.addPatient.PatientRequest
import com.example.neurosmg.patientTestList.patientProfile.PatientResponse
import com.example.neurosmg.patientTestList.patientProfile.UpdatePatientRequest
import com.example.neurosmg.preloader.PersonData
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

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
    suspend fun uploadFile(
        @Header("Authorization") authHeader: String,
        @Part file: MultipartBody.Part
    ): Response<List<UploadFileResponse>>

    @POST("/api/datafiles")
    suspend fun sendIdFile(
        @Header("Authorization") authHeader: String,
        @Body data: RequestSendIdFile
    ): Response<ResponseSendIds>

    @GET("/api/patients/{patientId}")
    suspend fun getArchivePatient(
        @Header("Authorization") authHeader: String,
        @Path("patientId") patientId: Int,
        @Query("populate") populate: String = "datafiles.file"
    ): Response<FileData>

    @GET("/api/patients")
    suspend fun getPatients(
        @Header("Authorization") authHeader: String
    ): Response<PersonData>

    @GET("/uploads/{fileName}")
    suspend fun downloadFile(
        @Path("fileName") patientId: String
    ): Response<ResponseBody>
}