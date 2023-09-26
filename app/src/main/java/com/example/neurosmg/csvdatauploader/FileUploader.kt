package com.example.neurosmg.csvdatauploader

import android.content.Context
import com.example.neurosmg.api.ApiService
import com.example.neurosmg.api.TokenController
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class FileUploader(
    private val context: Context
) {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(CONST_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiService = retrofit.create(ApiService::class.java)
    private val authToken: String? = TokenController(context).getUserToken()


    companion object  {
        private const val CONST_URL = "https://neuro.fdev.by/documentation/v1.0.0/"
    }


}
