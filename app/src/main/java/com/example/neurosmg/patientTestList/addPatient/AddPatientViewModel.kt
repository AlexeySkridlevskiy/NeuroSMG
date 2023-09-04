package com.example.neurosmg.patientTestList.addPatient

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.common.State
import com.example.neurosmg.login.RetrofitBuilder
import com.example.neurosmg.patientTestList.PatientViewState
import com.example.neurosmg.patientTestList.patientProfile.PatientResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPatientViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenController = TokenController(application.baseContext)

    private val retrofitBuilder = RetrofitBuilder()
    private val apiService = retrofitBuilder.retrofitCreate()

    var isAddPatient = false

    private val mutableAddPatient: MutableLiveData<State<PatientViewState>> = MutableLiveData()
    val patientAdded: LiveData<State<PatientViewState>> = mutableAddPatient

    fun addPatient(patientData: PatientData) {
        val jwtToken = tokenController.getUserToken()

        val addPatientRequest = PatientRequest(patientData)
        mutableAddPatient.value = State.Loading

        apiService.addPatient("Bearer $jwtToken", addPatientRequest)
            .enqueue(object : Callback<PatientResponse> {
                override fun onResponse(call: Call<PatientResponse>, response: Response<PatientResponse>) {
                    if (response.isSuccessful) {
                        isAddPatient = true
                        val addedPatientId: Int = response.body()?.data?.id ?: -1

                        val stateSuccess = PatientViewState(
                            isLoading = false,
                            showSuccessDialog = true,
                            addedPatientId = addedPatientId
                        )
                        mutableAddPatient.value = State.Success(stateSuccess)
                    } else {
                        val stateError = PatientViewState(
                            showErrorDialog = false
                        )
                        mutableAddPatient.value = State.Error(stateError)
                    }
                }

                override fun onFailure(call: Call<PatientResponse>, t: Throwable) {
                    val stateError = PatientViewState(
                        showErrorDialog = false,
                        exceptionMessage = t.message.toString()
                    )
                    mutableAddPatient.value = State.Error(stateError)
                }
            })
    }

    fun setEmptyStatus() {
        mutableAddPatient.value = State.Empty
    }
}