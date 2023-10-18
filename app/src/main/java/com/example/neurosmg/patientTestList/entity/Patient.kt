package com.example.neurosmg.patientTestList.entity

import com.example.neurosmg.patientTestList.PatientListResponse

data class Patient(val id: Int)

fun PatientListResponse?.mapToListOfPatients(): List<String> {
    val listOfPatients = mutableListOf<String>()

    this?.id_patient?.mapTo(listOfPatients) { patient ->
        patient.id.toString()
    }

    return listOfPatients
}
