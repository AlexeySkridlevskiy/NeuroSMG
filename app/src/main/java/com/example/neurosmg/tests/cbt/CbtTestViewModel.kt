package com.example.neurosmg.tests.cbt

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.neurosmg.csvdatauploader.UploadState
import com.example.neurosmg.data.local.db.NotSentDataDatabase
import com.example.neurosmg.data.repository.SendFilesDataSource
import com.example.neurosmg.data.useCase.SendFileUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class CbtTestViewModel(private val application: Application) : AndroidViewModel(application) {

    private val baseContext: Context = application.baseContext

    private val _uploadFileLiveData: MutableLiveData<UploadState> = MutableLiveData()
    val uploadFileLiveData: LiveData<UploadState> = _uploadFileLiveData

    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        _uploadFileLiveData.postValue(
            UploadState.Error(
                message = exception.message.toString()
            )
        )
    }

    fun sendFile(
        idPatient: Int,
        fileName: String,
        data: List<List<String>>
    ) {
        val dataStorageSendFiles = SendFilesDataSource(
            baseContext,
            database = NotSentDataDatabase.getInstance(context = baseContext).notSavedDataDao()
        )
        val uploadFileUseCase = SendFileUseCase(dataStorageSendFiles)

        viewModelScope.launch(exceptionHandler) {
            val sendFile = uploadFileUseCase.invoke(
                patientId = idPatient,
                fileName = fileName,
                data = data
            )

            _uploadFileLiveData.postValue(sendFile.value)
        }
    }

    fun setInitialState() {
        _uploadFileLiveData.value = UploadState.Initial
    }
}