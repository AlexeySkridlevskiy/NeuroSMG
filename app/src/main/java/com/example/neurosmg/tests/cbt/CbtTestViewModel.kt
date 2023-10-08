package com.example.neurosmg.tests.cbt

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.neurosmg.csvdatauploader.UploadState
import com.example.neurosmg.data.repository.DataStorageSendFiles
import com.example.neurosmg.data.useCase.SendFileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CbtTestViewModel(private val application: Application) : AndroidViewModel(application) {

    private val baseContext: Context = application.baseContext
    private val dataStorageSendFiles = DataStorageSendFiles(baseContext)
    private val uploadFileUseCase = SendFileUseCase(dataStorageSendFiles)

    private val _uploadFileLiveData: MutableLiveData<UploadState> = MutableLiveData()
    val uploadFileLiveData: LiveData<UploadState> = _uploadFileLiveData

    fun sendFile(idPatient: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val sendFile = uploadFileUseCase.invoke(
                patientId = idPatient,
                fileName = "output.csv" //todo: для теста CBT назовешь файл как тебе надо
            )

            _uploadFileLiveData.postValue(sendFile.value)
        }
    }

    fun setInitialState() {
        _uploadFileLiveData.value = UploadState.Initial
    }
}