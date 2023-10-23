package com.example.neurosmg.csvdatauploader

data class RequestSendIdFile(
    val data: BodyRequest
)

data class BodyRequest(
    val file: List<Int>,
    val patient: Int
)