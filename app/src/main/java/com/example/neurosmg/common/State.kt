package com.example.neurosmg.common

sealed class State<out T> {
    object Loading : State<Nothing>()
    data class Success<out T>(val data: T) : State<T>()
    data class Error<out T>(val data: T) : State<T>()
    object Empty : State<Nothing>()
}