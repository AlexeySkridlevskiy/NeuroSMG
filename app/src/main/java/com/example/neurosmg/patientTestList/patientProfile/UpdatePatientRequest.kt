package com.example.neurosmg.patientTestList.patientProfile

import com.google.gson.annotations.SerializedName

data class UpdatePatientRequest(
    @SerializedName("data") val data: UpdatePatientData
)

data class UpdatePatientData(
    @SerializedName("Birthday") val birthday: String,
    @SerializedName("Gender") val gender: String,
    @SerializedName("LeadHand") val leadHand: String,
    @SerializedName("Comment") val comment: String
)


