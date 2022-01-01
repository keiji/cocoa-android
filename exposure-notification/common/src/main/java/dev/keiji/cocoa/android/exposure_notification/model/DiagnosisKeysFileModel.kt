package dev.keiji.cocoa.android.exposure_notification.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diagnosis_keys_files")
data class DiagnosisKeysFileModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "exposure_data_id")
    var exposureDataId: Long,

    @ColumnInfo(name = "region")
    val region: String,

    @ColumnInfo(name = "subregion")
    val subregion: String?,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "created")
    val created: Long,

    @ColumnInfo(name = "state")
    var state: Int = State.None.ordinal,

    @ColumnInfo(name = "is_listed")
    var isListed: Boolean,
)

enum class State(val value: Int) {
    None(0),
    Downloaded(1),
    Processing(2),
    Completed(3),
}
