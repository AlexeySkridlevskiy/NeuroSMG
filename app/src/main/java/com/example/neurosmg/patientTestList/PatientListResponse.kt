package com.example.neurosmg.patientTestList

import com.google.gson.annotations.SerializedName
import java.util.Date

data class PatientListResponse(
    val id: Int,
    val username: String,
    val email: String,
    val provider: String,
    val confirmed: Boolean,
    val blocked: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val firstname: String?,
    val secondname: String?,
    val patronymic: String?,
    val attempts: Int?,
    val id_patient: List<PatientDetail>
)

data class PatientDetail(
    val id: Int,
    @SerializedName("Birthday") val birthday: String,
    @SerializedName("Gender") val gender: String,
    @SerializedName("LeadHand") val leadHand: String,
    @SerializedName("Comment") val comment: String?,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String?
)

