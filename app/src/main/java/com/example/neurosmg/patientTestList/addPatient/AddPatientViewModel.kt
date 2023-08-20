package com.example.neurosmg.patientTestList.addPatient

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.common.State
import com.example.neurosmg.login.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPatientViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenController = TokenController(application.baseContext)

    private val retrofitBuilder = RetrofitBuilder()
    private val apiService = retrofitBuilder.retrofitCreate()

    var isAddPatient = false

    private val mutableAddPatient: MutableLiveData<State<Boolean>> = MutableLiveData()
    val patientAdded: LiveData<State<Boolean>> = mutableAddPatient

    private val loadingLD: MutableLiveData<Boolean> = MutableLiveData(false)

    private fun addPatient(patientData: PatientData) {
        isAddPatient = false
        val jwtToken = tokenController.getUserToken()

        val addPatientRequest = PatientRequest(patientData)
        mutableAddPatient.value = State.Loading
        apiService.addPatient("Bearer $jwtToken", addPatientRequest).enqueue(object :
            Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    // Обработка успешного ответа
                    isAddPatient = true
                    Log.d("MyLog", "response success")
                    mutableAddPatient.value = State.Success(true)

                } else {
                    // Обработка ошибки
                    mutableAddPatient.value = State.Error
                    Log.d("MyLog", "response not success")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                mutableAddPatient.value = State.Error
            }
        })
    }

    fun isPatientAdd(patientData: PatientData): Boolean {
        loadingLD.postValue(true)
        addPatient(patientData)
        return isAddPatient
    }

}