package com.example.neurosmg.patientTestList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.neurosmg.api.IdController
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.login.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientsViewModel(application: Application) : AndroidViewModel(application) {
    private var listOfPatients = mutableListOf<Patient>()
    private val idController = IdController(application.baseContext)
    private val tokenController = TokenController(application.baseContext)
    private val retrofitBuilder = RetrofitBuilder()
    private val apiService = retrofitBuilder.retrofitCreate()
    private val userId = idController.getUserId()

    private val _userPatients = MutableLiveData<PatientListResponse>()
    val userPatients: LiveData<PatientListResponse> = _userPatients

    private fun fetchUserPatients(id: Int) {
        val jwtToken = tokenController.getUserToken()
        val call = apiService.getUserPatients(id, "Bearer $jwtToken")

        call.enqueue(object : Callback<PatientListResponse> {
            override fun onResponse(call: Call<PatientListResponse>, response: Response<PatientListResponse>) {
                if (response.isSuccessful) {
                    _userPatients.value = response.body()

                    _userPatients.value?.id_patient?.forEach { patientDetail ->
                        listOfPatients.add(Patient(patientDetail.id))
                    }
                    Log.d("MyLog", "$listOfPatients")
                } else {
                    Log.d("MyLog", "not Successful")
                }
            }

            override fun onFailure(call: Call<PatientListResponse>, t: Throwable) {
                // Обработка ошибок при сбое запроса
                Log.d("MyLog", "onFailure")
            }
        })
    }
    fun getListOfPatient(): List<Patient> {
        return listOfPatients
    }

    init {
        fetchUserPatients(userId)
    }
}