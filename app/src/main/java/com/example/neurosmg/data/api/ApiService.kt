package com.example.neurosmg.data.api

import FileData
import com.example.neurosmg.csvdatauploader.RequestSendIdFile
import com.example.neurosmg.csvdatauploader.ResponseSendIds
import com.example.neurosmg.csvdatauploader.UploadFileResponse
import com.example.neurosmg.doctorProfile.UserResponse
import com.example.neurosmg.login.entity.AuthData
import com.example.neurosmg.login.entity.AuthResponse
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
    ): Call<UserResponse>

    @GET("/api/users/{id}?populate=id_patient")
    fun getUserPatients(
        @Path("id") id: Int,
    ): Call<PatientListResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/patients")
    fun addPatient(
        @Body patientRequest: PatientRequest
    ): Call<PatientResponse>

    @GET("/api/patients/{id}")
    fun getPatientById(
        @Path("id") id: Int
    ): Call<PatientResponse>

    @PUT("/api/patients/{id}")
    fun updatePatient(
        @Path("id") id: Int,
        @Body updatePatientRequest: UpdatePatientRequest
    ): Call<PatientResponse>

    @Multipart
    @POST("/api/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): Response<List<UploadFileResponse>>

    @POST("/api/datafiles")
    suspend fun sendIdFile(
        @Body data: RequestSendIdFile
    ): Response<ResponseSendIds>

    @GET("/api/patients/{patientId}")
    suspend fun getArchivePatient(
        @Path("patientId") patientId: Int,
        @Query("populate") populate: String = "datafiles.file"
    ): Response<FileData>

    @GET("/api/patients")
    suspend fun getPatients(): Response<PersonData>

    @GET("/uploads/{fileName}")
    suspend fun downloadFile(
        @Path("fileName") patientId: String
    ): Response<ResponseBody>
}