package com.example.neurosmg.archive

import FileData

data class ArchiveState(
    val listOfArchive: List<FileTest> = mutableListOf(),
    val errorMessage: String? = null,
)

fun FileData?.mapToListOfNames(): List<FileTest> {

    val fileTests = mutableListOf<FileTest>()

    this
        ?.data
        ?.attributes
        ?.datafiles
        ?.data
        ?.mapNotNull {
            val name = it.attributes.file.data
                ?.firstOrNull()
                ?.attributes
                ?.name ?: ""

            val hash = it.attributes.file.data
                ?.firstOrNull()
                ?.attributes
                ?.hash ?: ""

            fileTests.add(
                FileTest(
                    name = name,
                    hash = hash
                )
            )

        }

    return fileTests
}
