package com.example.neurosmg.csvdatauploader

import com.example.neurosmg.api.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class FileUploader(private val baseUrl: String) {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiService = retrofit.create(ApiService::class.java)

    fun uploadFile(authToken: String, filePath: String, callback: (String?) -> Unit) {
        val file = File(filePath)
        val requestBody = file.asRequestBody("multipart/form-data".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData("files", file.name, requestBody)

        val call: Call<String> = apiService.uploadFile(authToken, multipartBody)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    // Обработка успешной загрузки файла
                    val uploadedFileUrl = response.body()
                    callback(uploadedFileUrl)
                } else {
                    // Обработка ошибки загрузки
                    callback(null)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // Обработка ошибки сети или запроса
                t.printStackTrace()
                callback(null)
            }
        })
    }
}
