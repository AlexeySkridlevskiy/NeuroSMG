package com.example.neurosmg.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userSessionManager = UserSessionManager(application.baseContext)
    private val repository = LoginRepository()

    fun tryToFindUser(
        login: String,
        password: String
    ): Boolean {
        val userFound = repository.login(login, password)

        if (userFound) {
            userSessionManager.saveUserLoggedIn(true)
            return true
        }
        return false
    }

    fun isUserLoggedIn(): Boolean {
        return userSessionManager.isUserLoggedIn()
    }
}