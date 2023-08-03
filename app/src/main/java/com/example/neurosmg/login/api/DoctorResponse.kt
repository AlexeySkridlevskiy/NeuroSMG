package com.example.neurosmg.login.api

data class DoctorResponse(
    val data: List<DoctorData>,
    val meta: MetaData
)

data class DoctorData(
    val id: Int,
    val attributes: DoctorAttributes
)

data class DoctorAttributes(
    val Firstname: String,
    val Secondname: String,
    val Patronymic: String,
    val Login: String,
    val Attempts: String,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String
)

data class MetaData(
    val pagination: PaginationData
)

data class PaginationData(
    val page: Int,
    val pageSize: Int,
    val pageCount: Int,
    val total: Int
)