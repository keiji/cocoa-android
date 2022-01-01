package dev.keiji.cocoa.android.exposure_notification.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureInformation

@Entity(tableName = "exposure_informations")
data class ExposureInformationModel(
    @PrimaryKey(autoGenerate = true) val id: Long,

    @ColumnInfo(name = "exposure_data_id")
    var exposureDataId: Long,

    @ColumnInfo(name = "attenuation_durations_in_millis") val attenuationDurationsInMillis: IntArray,
    @ColumnInfo(name = "attenuation_value") val attenuationValue: Int,
    @ColumnInfo(name = "date_millis_since_epoch") val dateMillisSinceEpoch: Long,
    @ColumnInfo(name = "duration_in_millis") val durationInMillis: Double,
    @ColumnInfo(name = "total_risk_score") val totalRiskScore: Int,
    @ColumnInfo(name = "transmission_risk_level") val transmissionRiskLevel: Int,
) {
    constructor(exposureInformation: ExposureInformation) : this(
        id = 0,
        exposureDataId = 0,
        attenuationDurationsInMillis = exposureInformation.attenuationDurationsInMillis,
        attenuationValue = exposureInformation.attenuationValue,
        dateMillisSinceEpoch = exposureInformation.dateMillisSinceEpoch,
        durationInMillis = exposureInformation.durationInMillis,
        totalRiskScore = exposureInformation.totalRiskScore,
        transmissionRiskLevel = exposureInformation.transmissionRiskLevel
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExposureInformationModel

        if (!attenuationDurationsInMillis.contentEquals(other.attenuationDurationsInMillis)) return false
        if (attenuationValue != other.attenuationValue) return false
        if (dateMillisSinceEpoch != other.dateMillisSinceEpoch) return false
        if (durationInMillis != other.durationInMillis) return false
        if (totalRiskScore != other.totalRiskScore) return false
        if (transmissionRiskLevel != other.transmissionRiskLevel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = attenuationDurationsInMillis.contentHashCode()
        result = 31 * result + attenuationValue
        result = 31 * result + dateMillisSinceEpoch.hashCode()
        result = 31 * result + durationInMillis.hashCode()
        result = 31 * result + totalRiskScore
        result = 31 * result + transmissionRiskLevel
        return result
    }
}
