package com.example.neurosmg.patientTestList

import com.example.neurosmg.patientTestList.entity.Patient

interface PatientOnClickListener {
    fun onItemClick(patient: Patient)
}