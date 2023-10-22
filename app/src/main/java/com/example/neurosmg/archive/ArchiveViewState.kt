package com.example.neurosmg.archive

import java.io.File

sealed class ArchiveViewState {
    data class SuccessDownloadFile(
        val file: File,
        val fileName: String
    ) : ArchiveViewState()

    data class ErrorDownloadFile(
        val message: String
    ) : ArchiveViewState()

    data class SuccessGetListFiles(
        val listOfArchive: List<FileTest> = mutableListOf()
    ) : ArchiveViewState()

    data class ErrorGetListFiles(
        val message: String
    ) : ArchiveViewState()

    data class EmptyDownloadedFile(
        val message: String
    ) : ArchiveViewState()
    object Loading : ArchiveViewState()
    object ListFromServerIsEmpty : ArchiveViewState()
}
