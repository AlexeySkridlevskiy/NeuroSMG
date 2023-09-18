package com.example.neurosmg.csvdatauploader

interface DataUploadCallback {
    fun onSuccess()
    fun onFailure(errorMessage: String)
}
