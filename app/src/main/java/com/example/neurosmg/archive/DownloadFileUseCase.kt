package com.example.neurosmg.archive

import android.content.Context
import com.example.neurosmg.api.ApiService
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS

class DownloadFileUseCase(
    private val apiService: ApiService,
) {
    suspend operator fun invoke(fileName: String) = flow<ArchiveViewState> {
        val result = apiService.downloadFile(fileName)

        emit(ArchiveViewState.Loading)
        if (result.isSuccessful) {
            val body = result.body()
            if (body != null) {
                val downloadDirectory = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
                val file = File(downloadDirectory, fileName)
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (body.byteStream().read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.close()
                body.close()
                emit(
                    ArchiveViewState.SuccessDownloadFile(
                        file = file,
                        fileName = fileName
                    )
                )
            } else {
                emit(
                    ArchiveViewState.EmptyDownloadedFile(
                        message = result.message()
                    )
                )
            }
        } else {
            emit(
                ArchiveViewState.ErrorDownloadFile(
                    message = result.message()
                )
            )
        }
    }
}
