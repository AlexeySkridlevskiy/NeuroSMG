package com.example.neurosmg.patientTestList.entity

import com.example.neurosmg.patientTestList.PatientListResponse

data class Patient(val id: Int)

fun PatientListResponse?.mapToListOfPatients(): List<Patient> {
    val listOfPatients = mutableListOf<Patient>()

    this?.id_patient?.mapTo(listOfPatients) { patient ->
        Patient(patient.id)
    }

    return listOfPatients
}
