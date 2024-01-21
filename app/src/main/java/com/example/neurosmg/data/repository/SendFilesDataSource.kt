package com.example.neurosmg.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neurosmg.csvdatauploader.BodyRequest
import com.example.neurosmg.csvdatauploader.RequestSendIdFile
import com.example.neurosmg.csvdatauploader.UploadState
import com.example.neurosmg.data.api.RetrofitBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class SendFilesDataSource(private val context: Context) {

    private val apiService = RetrofitBuilder(context).retrofitCreate()

    private val _uploadFileLiveData: MutableLiveData<UploadState> = MutableLiveData()
    val uploadFileLiveData: LiveData<UploadState> = _uploadFileLiveData

    suspend fun uploadFile(
        patientId: Int,
        fileName: String
    ) {
        val file = File(context.getExternalFilesDir(null), fileName)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaType())
        val body = MultipartBody.Part.createFormData("files", file.name, requestFile)


        val uploadFile = apiService.uploadFile(body)
        if (uploadFile.isSuccessful) {

            val fileId = uploadFile.body()?.first()?.id

            fileId?.let {
                _uploadFileLiveData.postValue(UploadState.Success.SuccessSendFile)
                sendIds(idPatient = patientId, idFile = fileId)
            }

        } else {
                _uploadFileLiveData.postValue(
                    UploadState.Error(uploadFile.message().toString())
                )
            }

    }

    private suspend fun sendIds(
        idPatient: Int,
        idFile: Int
    ) {
        val requestBody = RequestSendIdFile(
            data = BodyRequest(
                file = listOf(idFile),
                patient = idPatient
            )
        )

        val response = apiService.sendIdFile(requestBody)

        if (response.isSuccessful) {
            _uploadFileLiveData.postValue(UploadState.Success.SuccessSendFile)
        } else {
            _uploadFileLiveData.postValue(UploadState.Error("Простите, мы не смогли отправить файл. Попробуйте еще раз"))
        }
    }
}