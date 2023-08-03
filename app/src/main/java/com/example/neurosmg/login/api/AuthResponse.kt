package com.example.neurosmg.login.api

data class AuthResponse(
    val jwt: String,
    val user: User
)

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val provider: String,
    val confirmed: Boolean,
    val blocked: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val Firstname: String,
    val Secondname: String,
    val Patronymic: String,
    val Attempts: String
)

