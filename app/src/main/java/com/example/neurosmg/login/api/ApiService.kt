package com.example.neurosmg.login.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("doctors") // Замените "users" на ваше конкретное API endpoint для получения данных пользователей
    fun getUsers(): Call<List<UserData>>
}