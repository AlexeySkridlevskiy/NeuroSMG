package com.example.neurosmg.patientTestList

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.neurosmg.common.toFragment

class StatePatientViewModel: ViewModel() {

    private lateinit var lastFragment: Fragment
    private var navFromArchive: Boolean = false

    fun saveFragmentTest(testName: String) {
       lastFragment = testName.toFragment()
    }

    fun getSavedFragmentTest() = lastFragment

    fun setFlagFromArchive(fromArchive: Boolean) {
        navFromArchive = fromArchive
    }

    fun isNavFromArchive() = navFromArchive
}