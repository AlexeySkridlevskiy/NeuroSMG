package com.example.neurosmg.csvdatauploader

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.login.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class FileUploaderViewModel(private val application: Application) : AndroidViewModel(application) {

    val baseContext: Context = application.baseContext
    private val authToken = TokenController(baseContext).getUserToken()
    private val apiService = RetrofitBuilder().retrofitCreate()

    fun uploadFile(id: Int, successCallback: () -> Unit, errorCallback: (String) -> Unit) {
        val file = File(baseContext.getExternalFilesDir(null), "output.csv")
        val requestFile = file.asRequestBody("multipart/form-data".toMediaType())
        val body = MultipartBody.Part.createFormData("files", file.name, requestFile)

        if (authToken != null) {
            apiService.uploadFile("Bearer $authToken", body).enqueue(object :
                Callback<UploadFileResponse> {
                override fun onResponse(call: Call<UploadFileResponse>, response: Response<UploadFileResponse>) {
                    if (response.isSuccessful) {
                        successCallback.invoke()
                    } else {
                        errorCallback.invoke("Ошибка при загрузке файла")
                    }
                }

                override fun onFailure(call: Call<UploadFileResponse>, t: Throwable) {
                    errorCallback.invoke("Ошибка при загрузке файла: ${t.localizedMessage}")
                }
            })
        } else {
            errorCallback.invoke("Ошибка: authToken равен null")
        }
    }

}