package com.example.neurosmg.csvdatauploader

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.neurosmg.api.ApiService
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.login.RetrofitBuilder
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import java.io.File

private val retrofitBuilder = RetrofitBuilder()

class CSVDataUploader(application: Application) : AndroidViewModel(application) { // Принимаем TokenController

    private val tokenController = TokenController(application.baseContext)
    private val apiService = retrofitBuilder.retrofitCreate()

    fun generateAndUploadCSV(data: String, callback: DataUploadCallback) {
        try {
            val csvFileName = "data.csv"
            val csvBytes = data.toByteArray(Charsets.UTF_8)
            Log.d("MyLog", "csvFileName = $csvFileName")
            Log.d("MyLog", "csvBytes = $csvBytes")

            val requestFile = RequestBody.create("text/csv".toMediaTypeOrNull(), csvBytes)
            val body = MultipartBody.Part.createFormData("file", csvFileName, requestFile)

            uploadCSV(body, callback)
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onFailure(e.message ?: "Произошла ошибка")
        }
    }

    private fun uploadCSV(body: MultipartBody.Part, callback: DataUploadCallback) {
        val jwtToken = tokenController.getUserToken() // Получаем токен из TokenController
        val call = apiService.uploadCSV("Bearer $jwtToken", body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("MyLog", "response = $response")
                if (response.isSuccessful) {
                    Log.d("MyLog", "isSuccessful")
                    callback.onSuccess()
                } else {
                    callback.onFailure("Ошибка при загрузке данных на сервер")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                callback.onFailure("Ошибка при отправке данных на сервер")
            }
        })
    }
}
