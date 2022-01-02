package dev.keiji.cocoa.android.exposure_notification.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureSummary

@Entity(tableName = "exposure_summaries")
data class ExposureSummaryModel(
    @PrimaryKey(autoGenerate = true) var id: Long,

    @ColumnInfo(name = "exposure_data_id")
    var exposureDataId: Long,

    @ColumnInfo(name = "attenuation_durations_in_millis") val attenuationDurationsInMillis: IntArray,
    @ColumnInfo(name = "days_since_last_exposure") val daysSinceLastExposure: Int,
    @ColumnInfo(name = "matched_key_count") val matchedKeyCount: Int,
    @ColumnInfo(name = "maximum_risk_score") val maximumRiskScore: Int,
    @ColumnInfo(name = "summation_risk_score") val summationRiskScore: Int,
) {
    constructor(exposureSummary: ExposureSummary) : this(
        id = 0,
        exposureDataId = 0,
        attenuationDurationsInMillis = exposureSummary.attenuationDurationsInMillis,
        daysSinceLastExposure = exposureSummary.daysSinceLastExposure,
        matchedKeyCount = exposureSummary.matchedKeyCount,
        maximumRiskScore = exposureSummary.maximumRiskScore,
        summationRiskScore = exposureSummary.summationRiskScore,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExposureSummaryModel

        if (!attenuationDurationsInMillis.contentEquals(other.attenuationDurationsInMillis)) return false
        if (daysSinceLastExposure != other.daysSinceLastExposure) return false
        if (matchedKeyCount != other.matchedKeyCount) return false
        if (maximumRiskScore != other.maximumRiskScore) return false
        if (summationRiskScore != other.summationRiskScore) return false

        return true
    }

    override fun hashCode(): Int {
        var result = attenuationDurationsInMillis.contentHashCode()
        result = 31 * result + daysSinceLastExposure
        result = 31 * result + matchedKeyCount
        result = 31 * result + maximumRiskScore
        result = 31 * result + summationRiskScore
        return result
    }
}
