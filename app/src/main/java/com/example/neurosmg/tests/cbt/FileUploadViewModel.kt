package com.example.neurosmg.tests.cbt

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.login.RetrofitBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class FileUploaderViewModel(private val application: Application) : AndroidViewModel(application) {

    val baseContext = application.baseContext
    private val authToken: String? = TokenController(baseContext).getUserToken()
    private val apiService = RetrofitBuilder().retrofitCreate()

    fun uploadFile(callback: (String?) -> Unit) {
        val filePath = File(baseContext.getExternalFilesDir(null), "output.csv")

        val requestBody = filePath.asRequestBody("multipart/form-data".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData("output.csv", filePath.name, requestBody)

        if (authToken != null) {
            val call: Call<String> = apiService.uploadFile(authToken, multipartBody)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val uploadedFileUrl = response.body()
                        callback(uploadedFileUrl)

                    } else {
                        callback("${response.code()}: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    t.printStackTrace()
                    callback(t.localizedMessage)
                }
            })
        }
    }

}