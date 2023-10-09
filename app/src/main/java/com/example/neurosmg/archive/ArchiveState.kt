package com.example.neurosmg.archive

import com.example.neurosmg.data.entity.DataFile

data class ArchiveState(
    val listOfArchive: List<Int> = mutableListOf(),
    val errorMessage: String? = null
)
fun List<DataFile>?.mapToList(): List<Int> {
    val listOfArchiveId = mutableListOf<Int>()

    this?.map { file ->
        val archiveId = file.id
        listOfArchiveId.add(archiveId)
    }

    return listOfArchiveId
}
