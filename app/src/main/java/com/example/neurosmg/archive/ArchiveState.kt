package com.example.neurosmg.archive

import FileData

data class ArchiveState(
    val listOfArchive: List<String> = mutableListOf(),
    val errorMessage: String? = null
)

fun FileData?.mapToListOfNames(): List<String> {
    return this
        ?.data
        ?.attributes
        ?.datafiles
        ?.data
        ?.mapNotNull {
            it.attributes.file.data
                ?.firstOrNull()
                ?.attributes
                ?.name
        } ?: emptyList()
}
