import androidx.room.Entity
import androidx.room.PrimaryKey

data class FileData(
    val id: Int,
    val data: Data?
)
data class Data(
    val id: Int,
    val attributes: Attributes
)

data class Attributes(
    val Birthday: String,
    val Gender: String,
    val LeadHand: String,
    val Comment: String,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String,
    val datafiles: DataFiles
)

data class DataFiles(
    val data: List<DataFileItem>
)

data class DataFileItem(
    val id: Int,
    val attributes: FileAttributes
)

data class FileAttributes(
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String,
    val file: FileDetails
)

data class FileDetails(
    val data: List<DataItem>?
)

data class DataItem(
    val id: Int,
    val attributes: FileAttributesDetails
)

@Entity
data class FileAttributesDetails(
    @PrimaryKey val name: String,
    val hash: String
)