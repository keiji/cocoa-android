package dev.keiji.cocoa.android.exposure_notification.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "exposure_data")
data class ExposureDataBaseModel(
    @PrimaryKey(autoGenerate = true)
    var id: Long,

    @ColumnInfo(name = "region")
    val region: String,

    @ColumnInfo(name = "subregions")
    val subregionList: List<String>,

    @ColumnInfo(name = "en_version")
    val enVersion: String,

    @ColumnInfo(name = "state")
    var stateValue: Int = State.None.value,

    @ColumnInfo(name = "start_epoch")
    val startEpoch: Long,

    @ColumnInfo(name = "finish_epoch")
    var finishEpoch: Long = -1,
) {
    @ColumnInfo(name = "platform")
    var platform: String = "android"

    var state: State
        get() = when (stateValue) {
            State.Timeout.value -> State.Timeout
            State.Started.value -> State.Started
            State.ResultReceived.value -> State.ResultReceived
            State.Finished.value -> State.Finished
            else -> State.None
        }
        set(value) {
            stateValue = value.value
        }

    enum class State(
        val value: Int
    ) {
        Timeout(-1),
        None(0),
        Started(1),
        ResultReceived(2),
        Finished(3)
    }
}

data class ExposureDataModel(
    @Embedded val exposureBaseData: ExposureDataBaseModel,

    @Relation(
        parentColumn = "id",
        entityColumn = "exposure_data_id"
    )
    val diagnosisKeysFileList: List<DiagnosisKeysFileModel>,

    @Relation(
        parentColumn = "id",
        entityColumn = "exposure_data_id"
    )
    val dailySummaryList: MutableList<DailySummaryModel>,

    @Relation(
        entity = ExposureWindowModel::class,
        parentColumn = "id",
        entityColumn = "exposure_data_id"
    )
    val exposureWindowList: MutableList<ExposureWindowAndScanInstancesModel>,

    @Relation(
        parentColumn = "id",
        entityColumn = "exposure_data_id"
    )
    var exposureSummary: ExposureSummaryModel?,

    @Relation(
        parentColumn = "id",
        entityColumn = "exposure_data_id"
    )
    val exposureInformationList: MutableList<ExposureInformationModel>,
)
