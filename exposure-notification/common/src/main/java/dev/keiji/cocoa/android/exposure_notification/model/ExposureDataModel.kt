package dev.keiji.cocoa.android.exposure_notification.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "exposure_data")
data class ExposureDataBaseModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "region")
    val region: String,

    @ColumnInfo(name = "subregion")
    val subregion: String,

    @ColumnInfo(name = "en_version")
    val enVersion: String,

    @ColumnInfo(name = "start_epoch")
    val startEpoch: Long,

    @ColumnInfo(name = "finish_epoch")
    val finishEpoch: Long = -1,
) {
    @ColumnInfo(name = "platform")
    var platform: String = "android"
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
    val dailySummaryList: List<DailySummaryModel>,

    @Relation(
        entity = ExposureWindowModel::class,
        parentColumn = "id",
        entityColumn = "exposure_data_id"
    )
    val exposureWindowList: List<ExposureWindowModelAndScanInstances>,

    @Relation(
        parentColumn = "id",
        entityColumn = "exposure_data_id"
    )
    val exposureSummary: ExposureSummaryModel?,

    @Relation(
        parentColumn = "id",
        entityColumn = "exposure_data_id"
    )
    val exposureInformationList: List<ExposureInformationModel>,
)
