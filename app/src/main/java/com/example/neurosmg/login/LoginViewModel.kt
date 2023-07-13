package com.example.neurosmg.login

import androidx.lifecycle.ViewModel
import com.example.neurosmg.LoginRepository

class LoginViewModel : ViewModel() {

    private val repository = LoginRepository()

    fun canEnter(
        login: String,
        password: String
    ): Boolean {
        repository.login()
        return true
    }
}