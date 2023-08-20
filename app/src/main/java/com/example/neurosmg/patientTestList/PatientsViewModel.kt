package com.example.neurosmg.patientTestList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neurosmg.api.IdController
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.common.State
import com.example.neurosmg.login.RetrofitBuilder
import com.example.neurosmg.patientTestList.entity.Patient
import com.example.neurosmg.patientTestList.entity.mapToListOfPatients
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientsViewModel(application: Application) : AndroidViewModel(application) {

    private val idController = IdController(application.baseContext)
    private val tokenController = TokenController(application.baseContext)

    private val retrofitBuilder = RetrofitBuilder()
    private val apiService = retrofitBuilder.retrofitCreate()

    private val userId = idController.getUserId()

    private val _userPatients: MutableLiveData<State<List<Patient>>> = MutableLiveData()
    val userPatients: LiveData<State<List<Patient>>> = _userPatients

    private fun fetchUserPatients(id: Int) {
        val jwtToken = tokenController.getUserToken()
        val call = apiService.getUserPatients(id, "Bearer $jwtToken")

        call.enqueue(object : Callback<PatientListResponse> {
            override fun onResponse(
                call: Call<PatientListResponse>,
                response: Response<PatientListResponse>
            ) {
                _userPatients.value = State.Loading

                if (response.isSuccessful) {
                    Log.d("MyLog", "${response.body()?.id_patient}")
                    val listOfIdPatients = response.body().mapToListOfPatients()
                    _userPatients.value = State.Success(listOfIdPatients)
                } else {
                    _userPatients.value = State.Error
                }
            }

            override fun onFailure(call: Call<PatientListResponse>, t: Throwable) {
                _userPatients.value = State.Error
            }
        })
    }

    init {
        fetchUserPatients(userId)
    }
}