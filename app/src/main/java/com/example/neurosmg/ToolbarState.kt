package com.example.neurosmg

sealed class ToolbarState {
    object Initial: ToolbarState()
    object MainPage: ToolbarState()
    object DoctorProfile: ToolbarState()
}