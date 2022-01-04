package dev.keiji.cocoa.android.exposure_notification.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureWindow
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ScanInstance
import dev.keiji.cocoa.android.exposure_notification.uniqueKey
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.security.MessageDigest

@Entity(tableName = "exposure_windows")
data class ExposureWindowModel(
    @PrimaryKey(autoGenerate = true) var id: Long,

    @ColumnInfo(name = "unique_key")
    val uniqueKey: String,

    @ColumnInfo(name = "exposure_data_id")
    var exposureDataId: Long,

    @ColumnInfo(name = "calibration_confidence") val calibrationConfidence: Int,
    @ColumnInfo(name = "date_millis_since_epoch") val dateMillisSinceEpoch: Long,
    @ColumnInfo(name = "infectiousness") val infectiousness: Int,
    @ColumnInfo(name = "report_type") val reportType: Int,
) : Comparable<ExposureWindowModel> {
    val dateTime
        get() = DateTime(dateMillisSinceEpoch, DateTimeZone.UTC)

    constructor(
        exposureWindow: ExposureWindow
    ) : this(
        id = 0,
        uniqueKey = exposureWindow.uniqueKey(),
        exposureDataId = 0,
        calibrationConfidence = exposureWindow.calibrationConfidence,
        dateMillisSinceEpoch = exposureWindow.dateMillisSinceEpoch,
        infectiousness = exposureWindow.infectiousness,
        reportType = exposureWindow.reportType,
    )

    override fun compareTo(other: ExposureWindowModel): Int {
        return when {
            dateMillisSinceEpoch < other.dateMillisSinceEpoch -> -1
            dateMillisSinceEpoch > other.dateMillisSinceEpoch -> 1
            reportType < other.reportType -> -1
            reportType > other.reportType -> 1
            infectiousness < other.infectiousness -> +1
            infectiousness > other.infectiousness -> -1
            calibrationConfidence < other.calibrationConfidence -> +1
            calibrationConfidence > other.calibrationConfidence -> -1
            else -> 0
        }
    }
}

@Entity(tableName = "scan_instances")
data class ScanInstanceModel(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "exposure_window_id") var exposureWindowId: Long,
    @ColumnInfo(name = "min_attenuation_db") val minAttenuationDb: Int,
    @ColumnInfo(name = "seconds_since_last_scan") val secondsSinceLastScan: Int,
    @ColumnInfo(name = "typical_attenuation_db") val typicalAttenuationDb: Int,
) : Comparable<ScanInstanceModel> {
    constructor(
        scanInstance: ScanInstance
    ) : this(
        id = 0,
        exposureWindowId = 0,
        minAttenuationDb = scanInstance.minAttenuationDb,
        secondsSinceLastScan = scanInstance.secondsSinceLastScan,
        typicalAttenuationDb = scanInstance.typicalAttenuationDb,
    )

    override fun compareTo(other: ScanInstanceModel): Int {
        return when {
            minAttenuationDb < other.minAttenuationDb -> +1
            minAttenuationDb > other.minAttenuationDb -> -1
            secondsSinceLastScan < other.secondsSinceLastScan -> +1
            secondsSinceLastScan > other.secondsSinceLastScan -> -1
            typicalAttenuationDb < other.typicalAttenuationDb -> +1
            typicalAttenuationDb > other.typicalAttenuationDb -> -1
            else -> 0
        }
    }
}

data class ExposureWindowAndScanInstancesModel(
    @Embedded
    val exposureWindowModel: ExposureWindowModel,

    @Relation(
        parentColumn = "id",
        entityColumn = "exposure_window_id"
    )
    val scanInstances: List<ScanInstanceModel>,
) {
    constructor(
        exposureWindow: ExposureWindow
    ) : this(
        exposureWindowModel = ExposureWindowModel(exposureWindow),
        scanInstances = exposureWindow.scanInstances.map { si -> ScanInstanceModel(si) }
    )
}
