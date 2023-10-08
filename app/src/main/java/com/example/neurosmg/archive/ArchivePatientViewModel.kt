package com.example.neurosmg.archive

import GetArchive
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.neurosmg.common.State
import com.example.neurosmg.data.datasource.ArchivePatientDataSource
import kotlinx.coroutines.launch

class ArchivePatientViewModel(application: Application) : AndroidViewModel(application) {

    private val archivePatientDataSource = ArchivePatientDataSource(application.baseContext)
    private val getArchive = GetArchive(archivePatientDataSource)

    private val _archive: MutableLiveData<State<ArchiveState>> = MutableLiveData()
    val archive: LiveData<State<ArchiveState>> = _archive

    fun initPatientArchive(patientId: Int) {
        viewModelScope.launch {
            val result = getArchive.getArchivePatient(patientId)
            _archive.postValue(result.value)
        }
    }
}