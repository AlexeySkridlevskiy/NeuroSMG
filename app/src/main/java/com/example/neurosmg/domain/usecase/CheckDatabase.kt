package com.example.neurosmg.domain.usecase

import com.example.neurosmg.data.local.db.NotSentDataDao
import com.example.neurosmg.data.repository.SendFilesDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckDatabase(
    private val dao: NotSentDataDao,
    private val sendFilesDataSource: SendFilesDataSource
) {
    operator fun invoke() {
        CoroutineScope(Dispatchers.IO).launch {
            val notSavedTests = dao.getAllTests()

            notSavedTests.map { test ->
                sendFilesDataSource.uploadFile(
                    test.idPatient,
                    test.fileName,
                    test.data
                )
            }
        }
    }
}