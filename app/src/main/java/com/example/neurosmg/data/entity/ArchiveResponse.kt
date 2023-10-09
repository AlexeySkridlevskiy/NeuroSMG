package com.example.neurosmg.data.entity

data class ArchiveResponse(
    val data: ArchiveData,
)

data class ArchiveData(
    val id: Int,
    val attributes: Attributes
)

data class Attributes(
    val Birthday: String,
    val Gender: String,
    val LeadHand: String,
    val Comment: String,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String,
    val datafiles: DataFiles
)

data class DataFiles(
    val data: List<DataFile>
)

data class DataFile(
    val id: Int,
    val attributes: FileAttributes
)

data class FileAttributes(
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String
)
