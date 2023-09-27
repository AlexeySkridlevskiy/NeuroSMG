package com.example.neurosmg.csvdatauploader

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.login.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class FileUploaderViewModel(private val application: Application) : AndroidViewModel(application) {

    val baseContext = application.baseContext
    private val authToken: String? = TokenController(baseContext).getUserToken()
    private val apiService = RetrofitBuilder().retrofitCreate()

    fun uploadFile(id: Int) {

        val file = File(baseContext.getExternalFilesDir(null), "output.csv")
        val requestFile = file.asRequestBody("multipart/form-data".toMediaType())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        if (authToken != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    apiService.uploadFile(
                        authHeader = authToken,
                        file = body,
                        id = id
                    )
                } catch (e: Exception) {
                    Log.d("viewModelUploaderFile", "uploadFile: ${e.localizedMessage}")
                }
            }
        }
    }

}