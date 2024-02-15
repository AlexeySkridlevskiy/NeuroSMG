package com.example.neurosmg.utils

fun <T> List<T>.contentEquals(other: List<T>): Boolean {
    if (this.size != other.size) {
        return false
    }
    for (i in indices) {
        if (this[i] != other[i]) {
            return false
        }
    }
    return true
}