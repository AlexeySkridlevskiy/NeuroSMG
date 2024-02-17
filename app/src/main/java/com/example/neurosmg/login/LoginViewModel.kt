package com.example.neurosmg.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neurosmg.data.api.IdController
import com.example.neurosmg.data.api.TokenController
import com.example.neurosmg.common.State
import com.example.neurosmg.data.api.RetrofitBuilder
import com.example.neurosmg.login.entity.AuthData
import com.example.neurosmg.login.entity.AuthResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenController = TokenController(application.baseContext)
    private val idController = IdController(application.baseContext)
    private val retrofitBuilder = RetrofitBuilder(application.baseContext)
    private val apiService = retrofitBuilder.retrofitCreate(false)

    private val mutableLoginLD: MutableLiveData<State<String>> = MutableLiveData()
    val loginLD: LiveData<State<String>> = mutableLoginLD

    fun isUserLoggedIn(): Boolean {
        val tokenIsNotNull = !tokenController.getUserToken().isNullOrEmpty()

        if (tokenIsNotNull) {
            mutableLoginLD.value = State.Success(data = tokenController.getUserToken() ?: "")
            return true
        } else {
            mutableLoginLD.value = State.Error("")
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
                        mutableLoginLD.value = State.Success(data = authResponse.jwt)
                        tokenController.saveToken(authResponse.jwt)
                        idController.saveId(authResponse.user.id)
                    }

                } else {
                    mutableLoginLD.value = State.Error("")
                    tokenController.clearToken()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                mutableLoginLD.value = State.Error("")
            }
        })
    }
}