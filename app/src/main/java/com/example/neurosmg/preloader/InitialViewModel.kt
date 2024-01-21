package com.example.neurosmg.preloader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.neurosmg.data.api.RetrofitBuilder
import kotlinx.coroutines.launch

class InitialViewModel(application: Application) : AndroidViewModel(application) {

    private val retrofitBuilder = RetrofitBuilder(application.baseContext)

    private val _initialLiveData: MutableLiveData<PreloaderState> = MutableLiveData()
    val initialLiveData: LiveData<PreloaderState> = _initialLiveData
    fun sendTestRequest() {
        viewModelScope.launch {
            _initialLiveData.value = PreloaderState(state = PreloaderState.StateInitial.LOADING)

            try {
                val request = retrofitBuilder.retrofitCreate()
                    .getPatients()

                if (request.isSuccessful) {
                    _initialLiveData.value =
                        PreloaderState(state = PreloaderState.StateInitial.SUCCESS)
                } else {
                    _initialLiveData.value =
                        PreloaderState(state = PreloaderState.StateInitial.ERROR)
                }
            } catch (e: Exception) {
                _initialLiveData.value =
                    PreloaderState(state = PreloaderState.StateInitial.ERROR)
            }
        }
    }
}