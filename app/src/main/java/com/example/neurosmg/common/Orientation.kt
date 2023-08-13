package com.example.neurosmg.common

import androidx.fragment.app.Fragment

fun Fragment.setScreenOrientation(orientation: Int) {
    requireActivity().requestedOrientation = orientation
}