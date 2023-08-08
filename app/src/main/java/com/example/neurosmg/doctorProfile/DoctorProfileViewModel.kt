package com.example.neurosmg.doctorProfile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.neurosmg.api.LoginController
import com.example.neurosmg.login.RetrofitBuilder

class DoctorProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val loginController = LoginController(application.baseContext)
    private val retrofitBuilder = RetrofitBuilder()
    private val apiService = retrofitBuilder.retrofitCreate()

    fun getUsername(): String? {
        return loginController.getUserLogin()
    }
}