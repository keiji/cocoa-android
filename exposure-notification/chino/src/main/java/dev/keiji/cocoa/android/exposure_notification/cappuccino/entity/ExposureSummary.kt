package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.google.android.gms.nearby.exposurenotification.ExposureSummary as NativeExposureSummary

@Serializable
data class ExposureSummary(
    @SerialName("AttenuationDurationsInMillis") val attenuationDurationsInMillis: IntArray,
    @SerialName("DaysSinceLastExposure") val daysSinceLastExposure: Int,
    @SerialName("MatchedKeyCount") val matchedKeyCount: Int,
    @SerialName("MaximumRiskScore") val maximumRiskScore: Int,
    @SerialName("SummationRiskScore") val summationRiskScore: Int,
) {
    companion object {
        private const val ONE_MINUTE_IN_MILLIS = 1000 * 60

        private fun convertToMillis(array: IntArray): IntArray =
            array.map { it * ONE_MINUTE_IN_MILLIS }.toIntArray()
    }

    constructor(exposureSummary: NativeExposureSummary) : this(
        attenuationDurationsInMillis = convertToMillis(exposureSummary.attenuationDurationsInMinutes),
        daysSinceLastExposure = exposureSummary.daysSinceLastExposure,
        matchedKeyCount = exposureSummary.matchedKeyCount,
        maximumRiskScore = exposureSummary.maximumRiskScore,
        summationRiskScore = exposureSummary.summationRiskScore,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExposureSummary

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
