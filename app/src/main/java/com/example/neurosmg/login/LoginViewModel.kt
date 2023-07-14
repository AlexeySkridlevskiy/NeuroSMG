package com.example.neurosmg.login

import android.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    fun canEnter(
        login: String,
        password: String
    ): Boolean {
//        repository.login()
        return login == "1111" && password=="1111"

    }

    fun createAlertDialog(){

    }
}