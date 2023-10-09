package com.example.neurosmg.patientTestList.entity

import com.example.neurosmg.patientTestList.PatientListResponse

data class Patient(val id: Int)

fun PatientListResponse?.mapToListOfPatients(): List<Int> {
    val listOfPatients = mutableListOf<Int>()

    this?.id_patient?.mapTo(listOfPatients) { patient ->
        patient.id
    }

    return listOfPatients
}
