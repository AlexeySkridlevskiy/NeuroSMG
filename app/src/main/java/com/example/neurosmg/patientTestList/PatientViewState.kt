package com.example.neurosmg.patientTestList

data class PatientViewState(
    val isLoading: Boolean = false,
    val showErrorDialog: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val exceptionMessage: String = "",
    val addedPatientId: Int = -1,
)