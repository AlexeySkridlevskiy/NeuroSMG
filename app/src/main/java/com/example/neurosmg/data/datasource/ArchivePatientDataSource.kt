package com.example.neurosmg.data.datasource

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.archive.ArchiveState
import com.example.neurosmg.archive.mapToList
import com.example.neurosmg.common.State
import com.example.neurosmg.data.entity.ArchiveResponse
import com.example.neurosmg.login.RetrofitBuilder
import retrofit2.Response

class ArchivePatientDataSource(private val context: Context) {

    private val authToken = TokenController(context).getUserToken()
    private val apiService = RetrofitBuilder().retrofitCreate()

    suspend fun getArchivePatient(id: Int): Response<ArchiveResponse> {
        val archive = apiService.getArchivePatient(
            authHeader = "Bearer $authToken",
            patientId = id
        )

        return archive
    }

}