package com.example.neurosmg.csvdatauploader

data class RequestSendIdFile(
    val data: BodyRequest
)

data class BodyRequest(
    val fileIds: List<Int>,
    val patient: Int
)