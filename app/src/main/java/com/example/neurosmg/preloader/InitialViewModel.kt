package com.example.neurosmg.preloader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.neurosmg.api.TokenController
import com.example.neurosmg.login.RetrofitBuilder
import kotlinx.coroutines.launch

class InitialViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenController = TokenController(application.baseContext)
    private val retrofitBuilder = RetrofitBuilder()

    private val _initialLiveData: MutableLiveData<PreloaderState> = MutableLiveData()
    val initialLiveData: LiveData<PreloaderState> = _initialLiveData
    fun sendTestRequest() {
        viewModelScope.launch {
            _initialLiveData.value = PreloaderState(state = PreloaderState.StateInitial.LOADING)
            val request = retrofitBuilder.retrofitCreate()
                .getPatients("Bearer ${tokenController.getUserToken()}")

            if (request.isSuccessful) {
                _initialLiveData.value =
                    PreloaderState(state = PreloaderState.StateInitial.SUCCESS)
            } else {
                _initialLiveData.value =
                    PreloaderState(state = PreloaderState.StateInitial.ERROR)
            }
        }
    }
}