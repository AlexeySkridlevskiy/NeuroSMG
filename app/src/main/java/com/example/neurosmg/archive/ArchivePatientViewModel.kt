package com.example.neurosmg.archive

import GetArchive
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurosmg.data.datasource.ArchivePatientDataSource
import com.example.neurosmg.data.api.RetrofitBuilder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ArchivePatientViewModel(application: Application) : AndroidViewModel(application) {

    private val archivePatientDataSource = ArchivePatientDataSource(application.baseContext)
    private val getArchive = GetArchive(archivePatientDataSource)
    private val apiService = RetrofitBuilder(application.baseContext).retrofitCreate()
    private val downloadFileUseCase = DownloadFileUseCase(apiService)

    private val _archive: MutableSharedFlow<ArchiveViewState> = MutableSharedFlow()
    val archive: SharedFlow<ArchiveViewState> = _archive

    fun initPatientArchive(patientId: Int) {
        viewModelScope.launch {
            getArchive.getArchivePatient(patientId).collect {
                _archive.emit(it)
            }
        }
    }

    fun downloadSelectedFile(fileName: String) {
        viewModelScope.launch {
            downloadFileUseCase.invoke(fileName).collect {
                _archive.emit(it)
            }
        }
    }
}