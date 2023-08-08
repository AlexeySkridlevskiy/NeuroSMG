package com.example.neurosmg.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neurosmg.api.LoginController
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.common.State
import com.example.neurosmg.login.api.AuthData
import com.example.neurosmg.login.api.AuthResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenController = TokenController(application.baseContext)
    private val loginController = LoginController(application.baseContext)
    private val retrofitBuilder = RetrofitBuilder()
    private val apiService = retrofitBuilder.retrofitCreate()

    private val mutableLoginLD: MutableLiveData<State> = MutableLiveData()
    val loginLD: LiveData<State> = mutableLoginLD

    fun isUserLoggedIn(): Boolean {
        val tokenIsNotNull = !tokenController.getUserToken().isNullOrEmpty()

        if (tokenIsNotNull) {
            mutableLoginLD.value = State.Success
            return true
        }

        return false
    }

    fun login(
        login: String,
        password: String
    ) {

        val authData = AuthData(login, password)
        mutableLoginLD.value = State.Loading
        apiService.login(authData).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(
                call: Call<AuthResponse>,
                response: Response<AuthResponse>
            ) {
                if (response.isSuccessful) {
                    val authResponse = response.body()

                    if (authResponse?.jwt?.isNotEmpty() == true) {
                        mutableLoginLD.value = State.Success
                        tokenController.saveToken(authResponse.jwt)
                        loginController.saveLogin(authResponse.user.username)
                    }

                } else {
                    mutableLoginLD.value = State.Error
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                mutableLoginLD.value = State.Error
            }
        })
    }
}