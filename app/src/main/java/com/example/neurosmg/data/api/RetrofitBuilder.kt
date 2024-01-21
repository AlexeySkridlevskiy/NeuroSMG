package com.example.neurosmg.data.api

import android.content.Context
import com.example.neurosmg.api.ApiService
import com.example.neurosmg.api.TokenController
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder(val context: Context) {

    private val tokenController = TokenController(context)

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val newRequestBuilder: Request.Builder = originalRequest.newBuilder()
                .addHeader(AUTH_KEY, "$AUTH_KEY_START ${tokenController.getUserToken()}")
            val newRequest = newRequestBuilder.build()
            chain.proceed(newRequest)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    fun retrofitCreate(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    companion object {
        private const val BASE_URL = "https://neuro.fdev.by"
        private const val AUTH_KEY = "Authorization"
        private const val AUTH_KEY_START = "Bearer"
    }
}