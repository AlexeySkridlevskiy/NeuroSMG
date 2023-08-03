package com.example.neurosmg.login

import com.example.neurosmg.login.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {
    fun retrofitCreate(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://neuro.fdev.by")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}