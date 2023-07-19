package com.example.neurosmg.patientTestList

import androidx.lifecycle.ViewModel

class PatientsViewModel : ViewModel() {

    private val listOfPatients = mutableListOf<Patient>()

    fun getListOfPatient(): List<Patient> {
        return listOfPatients
    }

    init {
        for (i in 1..16) {
            listOfPatients.add(Patient(i.toString()))
        }
    }
}