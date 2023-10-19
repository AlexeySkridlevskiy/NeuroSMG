package com.example.neurosmg.preloader

data class PreloaderState(
    val state: StateInitial
) {
    enum class StateInitial {
        LOADING, SUCCESS, ERROR
    }
}
