import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.neurosmg.archive.ArchiveState
import com.example.neurosmg.archive.mapToList
import com.example.neurosmg.common.State
import com.example.neurosmg.data.datasource.ArchivePatientDataSource

class GetArchive(
    private val archivePatientDataSource: ArchivePatientDataSource
) {

    private val archiveLiveData: MutableLiveData<State<ArchiveState>> = MutableLiveData()

    suspend fun getArchivePatient(patientId: Int): LiveData<State<ArchiveState>> {

        val archive = archivePatientDataSource.getArchivePatient(patientId)

        archiveLiveData.value = State.Loading

        if (archive.isSuccessful) {
            val archiveIds = archive.body()
                ?.data
                ?.attributes
                ?.datafiles
                ?.data
                ?.mapToList()

            if (archiveIds.isNullOrEmpty()) {
                archiveLiveData.value = State.Empty
            } else {
                val successState = State.Success(
                    data = ArchiveState(
                        listOfArchive = archiveIds
                    )
                )
                archiveLiveData.value = successState
            }
        } else {
            val errorState = State.Error(
                data = ArchiveState(
                    errorMessage = archive.message()
                )
            )
            archiveLiveData.value = errorState
        }

        return archiveLiveData
    }
}