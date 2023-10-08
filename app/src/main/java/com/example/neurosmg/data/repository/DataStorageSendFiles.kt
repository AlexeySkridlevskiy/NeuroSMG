package com.example.neurosmg.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.csvdatauploader.BodyRequest
import com.example.neurosmg.csvdatauploader.RequestSendIdFile
import com.example.neurosmg.csvdatauploader.UploadFileResponse
import com.example.neurosmg.csvdatauploader.UploadState
import com.example.neurosmg.login.RetrofitBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DataStorageSendFiles(private val context: Context) {

    private val authToken = TokenController(context).getUserToken()
    private val apiService = RetrofitBuilder().retrofitCreate()

    private val _uploadFileLiveData: MutableLiveData<UploadState> = MutableLiveData()
    val uploadFileLiveData: LiveData<UploadState> = _uploadFileLiveData

    suspend fun uploadFile(
        patientId: Int,
        fileName: String
    ) {
        val file = File(context.getExternalFilesDir(null), fileName)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaType())
        val body = MultipartBody.Part.createFormData("files", file.name, requestFile)

        if (authToken != null) {
            val uploadFile = apiService.uploadFile("Bearer $authToken", body)
            if (uploadFile.isSuccessful) {

                val fileId = uploadFile.body()?.first()?.id

                if (fileId != null) {
                    _uploadFileLiveData.postValue(UploadState.Success.SuccessGetIdFile(idFile = fileId))

                    sendIds(idPatient = patientId, idFile = fileId)
                } else {
                    _uploadFileLiveData.postValue(UploadState.Error("file id is null or zero"))
                }

            } else {
                _uploadFileLiveData.postValue(UploadState.Error(uploadFile.message().toString()))
            }

        } else {
            _uploadFileLiveData.postValue(UploadState.Error("Error: authToken is null"))
        }
    }

    private suspend fun sendIds(
        idPatient: Int,
        idFile: Int
    ) {
        val requestBody = RequestSendIdFile(
            data = BodyRequest(
                fileIds = listOf(idFile),
                patient = idPatient
            )
        )

        val response = apiService.sendIdFile("Bearer $authToken", requestBody)

        if (response.isSuccessful) {
            _uploadFileLiveData.postValue(UploadState.Success.SuccessSendFile)
        } else {
            _uploadFileLiveData.postValue(UploadState.Error("Простите, мы не смогли отправить файл. Попробуйте еще раз"))
        }
    }
}