package com.example.neurosmg.patientTestList.patientProfile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neurosmg.api.ApiService
import com.example.neurosmg.common.State
import com.example.neurosmg.login.RetrofitBuilder

class PatientProfileViewModel(application: Application) : AndroidViewModel(application) {

    //private val apiClient = ApiService.getInstance(application)

    private val _patientData: MutableLiveData<State<PatientResponse>> = MutableLiveData()
    val patientData: LiveData<State<PatientResponse>> = _patientData

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun fetchPatientData(patientId: Int) {
//        _loading.value = true
//
//        apiClient.getPatientById(patientId) { response ->
//            _loading.value = false
//
//            if (response.isSuccessful) {
//                _patientData.value = State.Success(response.body())
//            } else {
//                _patientData.value = State.Error
//            }
//        }
    }
}
