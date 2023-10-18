package com.example.neurosmg.data.datasource

import FileData
import android.content.Context
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.login.RetrofitBuilder
import retrofit2.Response

class ArchivePatientDataSource(private val context: Context) {

    private val authToken = TokenController(context).getUserToken()
    private val apiService = RetrofitBuilder().retrofitCreate()

    suspend fun getArchivePatient(id: Int): Response<FileData> {
        val archive = apiService.getArchivePatient(
            authHeader = "Bearer $authToken",
            patientId = id
        )

        return archive
    }

}