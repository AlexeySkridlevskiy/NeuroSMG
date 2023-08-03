package com.example.neurosmg.common

sealed class State {
    object Loading : State()
    object Success : State()
    object Error : State()
}