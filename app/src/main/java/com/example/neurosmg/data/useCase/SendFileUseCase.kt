package com.example.neurosmg.data.useCase

import androidx.lifecycle.LiveData
import com.example.neurosmg.csvdatauploader.UploadState
import com.example.neurosmg.data.repository.DataStorageSendFiles

class SendFileUseCase(
    private val dataStorageSendFiles: DataStorageSendFiles
) {
    suspend operator fun invoke(fileName: String, patientId: Int): LiveData<UploadState> {
        dataStorageSendFiles.uploadFile(
            patientId = patientId,
            fileName = fileName
        )

        return dataStorageSendFiles.uploadFileLiveData
    }
}