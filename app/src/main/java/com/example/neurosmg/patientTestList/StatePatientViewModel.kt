package com.example.neurosmg.patientTestList

import androidx.lifecycle.ViewModel

class StatePatientViewModel : ViewModel() {

    private var statePatientList = StatePatientList(
        navigateTo = ScreenNavigationMenu.TO_CHOOSED_TEST,
        navigateToTest = null
    )

    fun getStatePatientList(): StatePatientList = statePatientList

    fun navTo(screenNavigationMenu: ScreenNavigationMenu) {
        val state = statePatientList.copy(
            navigateTo = screenNavigationMenu
        )
        statePatientList = state
    }
}

data class StatePatientList(
    val navigateTo: ScreenNavigationMenu,
    val navigateToTest: String? = null
)

enum class ScreenNavigationMenu {
    TO_ARCHIVE,
    TO_PATIENT_LIST,
    TO_CHOOSED_TEST,
    TO_QUESTIONNAIRE
}