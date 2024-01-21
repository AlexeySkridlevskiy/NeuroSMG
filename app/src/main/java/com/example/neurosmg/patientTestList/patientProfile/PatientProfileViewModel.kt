package com.example.neurosmg.patientTestList.patientProfile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.common.State
import com.example.neurosmg.data.api.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val retrofitBuilder = RetrofitBuilder(application.baseContext)
    private val apiService = retrofitBuilder.retrofitCreate()

    private val _patientData: MutableLiveData<State<PatientProfileState>> = MutableLiveData()
    val patientData: LiveData<State<PatientProfileState>> = _patientData
    var patientResponce: PatientResponse? = null
    private fun fetchPatientById(id: Int) {
        _patientData.value = State.Loading

        apiService.getPatientById(id).enqueue(object : Callback<PatientResponse> {
            override fun onResponse(call: Call<PatientResponse>, response: Response<PatientResponse>) {
                if (response.isSuccessful) {
                    patientResponce = response.body()
                    val stateSuccess = PatientProfileState(
                        birthday = patientResponce?.data?.attributes?.Birthday,
                        gender = patientResponce?.data?.attributes?.Gender,
                        leadHand = patientResponce?.data?.attributes?.LeadHand,
                        comment = patientResponce?.data?.attributes?.Comment
                    )
                    _patientData.value = State.Success(stateSuccess)
                } else {
                    val stateError = PatientProfileState(
                        errorMessage = response.errorBody().toString()
                    )
                    _patientData.value = State.Error(stateError)
                }
            }

            override fun onFailure(call: Call<PatientResponse>, t: Throwable) {
                val stateError = PatientProfileState(
                    errorMessage = t.message
                )
                _patientData.value = State.Error(stateError)
            }
        })
    }

    fun updatePatientData(updatedData: UpdatePatientRequest, id: Int) {
        _patientData.value = State.Loading

        apiService.updatePatient(id, updatedData).enqueue(object : Callback<PatientResponse> {
            override fun onResponse(call: Call<PatientResponse>, response: Response<PatientResponse>) {
                if (response.isSuccessful) {
                    val stateSuccess = PatientProfileState(
                        birthday = updatedData.data.birthday,
                        gender = updatedData.data.gender,
                        leadHand = updatedData.data.leadHand,
                        comment = updatedData.data.comment
                    )
                    _patientData.value = State.Success(stateSuccess)
                } else {
                    val stateError = PatientProfileState(
                        errorMessage = response.errorBody().toString()
                    )
                    _patientData.value = State.Error(stateError)
                }
            }

            override fun onFailure(call: Call<PatientResponse>, t: Throwable) {
                val stateError = PatientProfileState(
                    errorMessage = t.message
                )
                _patientData.value = State.Error(stateError)
            }
        })
    }

    fun getPatientById(id: Int): PatientResponse? {
        fetchPatientById(id)

        return patientResponce
    }
}
