package com.example.neurosmg.csvdatauploader

data class ResponseSendIds(
    val dataIds: DataIds
)

data class DataIds(
    val id: Int,
    val attributes: Attributes
)

data class Attributes(
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String
)




