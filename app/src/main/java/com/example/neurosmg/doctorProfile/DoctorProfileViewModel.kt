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
    private var username = ""

    val userLiveData: MutableLiveData<UserResponse> = MutableLiveData()

    private val mutableIdLD: MutableLiveData<State<Boolean>> = MutableLiveData()
    val idLD: LiveData<State<Boolean>> = mutableIdLD

    val loadingLD: MutableLiveData<Boolean> = MutableLiveData(false)

    private fun getUserInfo(id: Int) {
        val jwtToken = tokenController.getUserToken()
        val call: Call<UserResponse> = apiService.getUserById(id, "Bearer $jwtToken")

        mutableIdLD.value = State.Loading

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse: UserResponse? = response.body()
                    userLiveData.postValue(userResponse)
                    if (userResponse != null) {
                        mutableIdLD.value = State.Success(true)
                        username = userResponse.username
                    }
                } else {
                    mutableIdLD.value = State.Error
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                mutableIdLD.value = State.Error
            }
        })
    }
    fun getUsername(): String {
        loadingLD.postValue(true)
        getUserInfo(userId)
        return username
    }
}