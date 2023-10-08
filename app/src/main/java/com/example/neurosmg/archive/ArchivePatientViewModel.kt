package com.example.neurosmg.archive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.neurosmg.common.State

class ArchivePatientViewModel : ViewModel() {



    private val _userPatients: MutableLiveData<State<List<ArchiveId>>> = MutableLiveData()
    val userPatients: LiveData<State<List<ArchiveId>>> = _userPatients

    fun initPatientArchive() {

    }
}