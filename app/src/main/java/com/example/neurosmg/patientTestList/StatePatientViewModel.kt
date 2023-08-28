package com.example.neurosmg.patientTestList

import androidx.lifecycle.ViewModel

class StatePatientViewModel : ViewModel() {

    private var statePatientList = StatePatientList(
        navigateTo = ScreenNavigationMenu.TO_TESTS,
        navigateToTest = null
    )

    fun getStatePatientList(): StatePatientList = statePatientList

    fun navToArchive() {
        val archive = statePatientList.copy(
            navigateTo = ScreenNavigationMenu.TO_ARCHIVE
        )
        statePatientList = archive
    }

    fun navToPatientList() {
        val archive = statePatientList.copy(
            navigateTo = ScreenNavigationMenu.TO_PATIENT_LIST
        )
        statePatientList = archive
    }

    fun navToTests(test: String) {
        val archive = statePatientList.copy(
            navigateTo = ScreenNavigationMenu.TO_TESTS,
            navigateToTest = test
        )
        statePatientList = archive
    }
}

data class StatePatientList(
    val navigateTo: ScreenNavigationMenu,
    val navigateToTest: String? = null
)

enum class ScreenNavigationMenu {
    TO_ARCHIVE,
    TO_TESTS,
    TO_PATIENT_LIST
}