package com.example.neurosmg.patientTestList.addPatient

data class PatientRequest(
    val data: PatientData
)

data class PatientData(
    val Birthday: String,
    val Gender: String,
    val LeadHand: String,
    val Comment: String,
    val user_id_patient: Int
)