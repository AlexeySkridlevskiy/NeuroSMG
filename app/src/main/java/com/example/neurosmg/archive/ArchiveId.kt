package com.example.neurosmg.archive

import com.example.neurosmg.patientTestList.PatientListResponse

data class ArchiveId(val id: Int)

fun PatientListResponse?.mapToList(): List<ArchiveId> {
    val listOfArchiveId = mutableListOf<ArchiveId>()

    this?.id_patient?.mapTo(listOfArchiveId) { archiveId ->
        ArchiveId(archiveId.id)
    }

    return listOfArchiveId
}
