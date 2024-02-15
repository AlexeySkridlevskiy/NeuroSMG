package com.example.neurosmg.tests.sct.domain

interface SctRepository {
    fun showRandomWord(): String

    fun saveData()

    fun calculateTouchDuration(
        touchEndTimeMillis: Long,
        touchStartTimeMillis: Long,
    )
}