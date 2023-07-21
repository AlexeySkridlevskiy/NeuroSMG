package com.example.neurosmg

sealed class ToolbarState {
    object Initial: ToolbarState()
    object MainPage: ToolbarState()
    object DoctorProfile: ToolbarState()
    object TestPage: ToolbarState()
    object PatientList: ToolbarState()
    object FOTTest: ToolbarState()
    object RATTest: ToolbarState()
    object IATTest: ToolbarState()
    object GNGTest: ToolbarState()
    object SCTTest: ToolbarState()
    object TMTTest: ToolbarState()
    object CBTTest: ToolbarState()
    object MRTTest: ToolbarState()
    object PatientProfile: ToolbarState()
    object Archive: ToolbarState()
    object AboutProgramPage: ToolbarState()
}