package com.example.neurosmg.login

import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    fun canEnter(
        login: String,
        password: String
    ): Boolean {
//        repository.login()
//        return login == "1111" && password=="1111"
        return true
    }

}