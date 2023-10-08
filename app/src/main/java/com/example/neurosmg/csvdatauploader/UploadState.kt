package com.example.neurosmg.csvdatauploader

sealed class UploadState {
    object Loading : UploadState()

    sealed class Success : UploadState() {
        data class SuccessGetIdFile(val idFile: Int) : Success()
        object SuccessSendFile : Success()
    }

    data class Error(val message: String) : UploadState()
    object Initial : UploadState()
}