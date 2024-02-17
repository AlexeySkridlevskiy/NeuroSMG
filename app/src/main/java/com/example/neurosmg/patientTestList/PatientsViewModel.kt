package com.example.neurosmg.patientTestList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neurosmg.data.api.IdController
import com.example.neurosmg.common.State
import com.example.neurosmg.data.api.RetrofitBuilder
import com.example.neurosmg.patientTestList.entity.mapToListOfPatients
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientsViewModel(application: Application) : AndroidViewModel(application) {

    private val idController = IdController(application.baseContext)
    private val retrofitBuilder = RetrofitBuilder(application.baseContext)
    private val apiService = retrofitBuilder.retrofitCreate()

    private val userId = idController.getUserId()

    private val _userPatients: MutableLiveData<State<List<String>>> = MutableLiveData()
    val userPatients: LiveData<State<List<String>>> = _userPatients

    fun fetchUserPatients() {
            val call = apiService.getUserPatients(userId)
            call.enqueue(object : Callback<PatientListResponse> {
                override fun onResponse(
                    call: Call<PatientListResponse>,
                    response: Response<PatientListResponse>
                ) {
                    _userPatients.value = State.Loading
                    Log.d("getUserPatients", "onFailure: ")

                    if (response.isSuccessful) {
                        val listOfIdPatients = response.body().mapToListOfPatients()
                        _userPatients.value = State.Success(listOfIdPatients)
                    } else {
                        _userPatients.value = State.Error(emptyList())
                    }
                }

                override fun onFailure(call: Call<PatientListResponse>, t: Throwable) {
                    Log.d("getUserPatients", "onFailure: $t")

                    _userPatients.value = State.Error(emptyList())
                }
            })
    }
}