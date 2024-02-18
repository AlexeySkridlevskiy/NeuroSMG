package com.example.neurosmg.data.useCase

import androidx.lifecycle.LiveData
import com.example.neurosmg.csvdatauploader.UploadState
import com.example.neurosmg.data.repository.SendFilesDataSource

class SendFileUseCase(
    private val dataStorageSendFiles: SendFilesDataSource
) {
    suspend operator fun invoke(
        fileName: String,
        patientId: Int,
        data: List<List<String>>
    ): LiveData<UploadState> {
        dataStorageSendFiles.uploadFile(
            patientId = patientId,
            fileName = fileName,
            data = data
        )

        return dataStorageSendFiles.uploadFileLiveData
    }
}