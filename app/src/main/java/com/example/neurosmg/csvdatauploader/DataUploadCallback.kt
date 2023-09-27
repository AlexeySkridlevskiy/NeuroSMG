package com.example.neurosmg.csvdatauploader

sealed class DataUploadCallback {
    object OnSuccess : DataUploadCallback()
    object OnFailure : DataUploadCallback()
}
