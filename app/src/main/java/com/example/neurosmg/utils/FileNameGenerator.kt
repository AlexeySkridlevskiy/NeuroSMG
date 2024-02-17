package com.example.neurosmg.utils

fun generateName(
    testName: String
): String {
    val unixTime = System.currentTimeMillis()
    return "$testName.${unixTime}${TEST_FILE_EXTENSION}"
}

private const val TEST_FILE_EXTENSION = ".csv"
