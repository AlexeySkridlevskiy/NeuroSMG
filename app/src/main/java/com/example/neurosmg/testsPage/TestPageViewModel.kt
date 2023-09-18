package com.example.neurosmg.testsPage

import androidx.lifecycle.ViewModel

class TestPageViewModel : ViewModel() {

    private val tests = listOf(
        TestItem("FOT"),
        TestItem("RAT"),
        TestItem("IAT"),
        TestItem("IAT 2"),
        TestItem("GNG"),
        TestItem("SCT"),
        TestItem("TMT"),
        TestItem("CBT"),
        TestItem("MRT")
    )

    fun getTests(): List<TestItem> {
        return tests
    }

}