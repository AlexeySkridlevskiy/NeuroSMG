package com.example.neurosmg.csvdatauploader

data class UploadFileResponse(
    val id: Int,
    val name: String,
    val alternativeText: String?,
    val caption: String?,
    val width: Int?,
    val height: Int?,
    val formats: Any?,
    val hash: String,
    val ext: String,
    val mime: String,
    val size: Double,
    val url: String,
    val previewUrl: String?,
    val provider: String,
    val provider_metadata: Any?,
    val createdAt: String,
    val updatedAt: String
)
