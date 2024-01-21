package com.example.neurosmg.data.datasource

import FileData
import android.content.Context
import com.example.neurosmg.data.api.RetrofitBuilder
import retrofit2.Response

class ArchivePatientDataSource(private val context: Context) {

    private val apiService = RetrofitBuilder(context).retrofitCreate()

    suspend fun getArchivePatient(id: Int): Response<FileData> = apiService.getArchivePatient(
        patientId = id
    )
}