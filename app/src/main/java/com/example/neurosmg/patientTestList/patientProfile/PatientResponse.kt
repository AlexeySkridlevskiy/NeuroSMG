package com.example.neurosmg.patientTestList.patientProfile

data class PatientResponse(
    val data: PatientData,
    val meta: Any // Здесь может быть какая-то мета-информация, если она есть в вашем API
)

data class PatientData(
    val id: Int,
    val attributes: PatientAttributes
)

data class PatientAttributes(
    val Birthday: String,
    val Gender: String,
    val LeadHand: String,
    val Comment: String,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String
)

