package com.example.neurosmg.doctorProfile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neurosmg.api.IdController
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.common.State
import com.example.neurosmg.login.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val idController = IdController(application.baseContext)
    private val tokenController = TokenController(application.baseContext)
    private val retrofitBuilder = RetrofitBuilder()
    private val apiService = retrofitBuilder.retrofitCreate()
    private val userId = idController.getUserId()

    val userLiveData: MutableLiveData<UserResponse> = MutableLiveData()

    private val _profileLD: MutableLiveData<State<ProfileDoctorState>> = MutableLiveData()
    val profileLD: LiveData<State<ProfileDoctorState>> = _profileLD

    fun getUserInfo() {
        val jwtToken = tokenController.getUserToken()
        val call: Call<UserResponse> = apiService.getUserById(userId, "Bearer $jwtToken")

        _profileLD.value = State.Loading

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse: UserResponse? = response.body()
                    userLiveData.postValue(userResponse)
                    if (userResponse != null) {
                        val stateSuccess = ProfileDoctorState(
                            username = userResponse.username
                        )
                        _profileLD.value = State.Success(stateSuccess)
                    }
                } else {
                    val stateError = ProfileDoctorState(
                        errorMessage = response.errorBody().toString()
                    )
                    _profileLD.value = State.Error(stateError)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                val stateError = ProfileDoctorState(
                    errorMessage = t.message
                )
                _profileLD.value = State.Error(stateError)
            }
        })
    }
}