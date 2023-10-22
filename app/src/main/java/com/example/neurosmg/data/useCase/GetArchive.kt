import com.example.neurosmg.archive.ArchiveState
import com.example.neurosmg.archive.ArchiveViewState
import com.example.neurosmg.archive.mapToListOfNames
import com.example.neurosmg.common.State
import com.example.neurosmg.data.datasource.ArchivePatientDataSource
import kotlinx.coroutines.flow.flow

class GetArchive(
    private val archivePatientDataSource: ArchivePatientDataSource
) {

    suspend fun getArchivePatient(patientId: Int) = flow<ArchiveViewState>() {

        val archive = archivePatientDataSource.getArchivePatient(patientId)

        val loading = ArchiveViewState.Loading
        emit(loading)

        if (archive.isSuccessful) {
            val archiveIds = archive.body().mapToListOfNames()

            if (archiveIds.isEmpty()) {
                val emptyArchive = ArchiveViewState.ListFromServerIsEmpty
                emit(emptyArchive)
            } else {
                val successState = State.Success(
                    data = ArchiveState(
                        listOfArchive = archiveIds
                    )
                )
                val success = ArchiveViewState.SuccessGetListFiles(successState.data.listOfArchive)
                emit(success)
            }
        } else {
            val errorState = ArchiveViewState.ErrorGetListFiles(archive.message())
            emit(errorState)
        }
    }
}