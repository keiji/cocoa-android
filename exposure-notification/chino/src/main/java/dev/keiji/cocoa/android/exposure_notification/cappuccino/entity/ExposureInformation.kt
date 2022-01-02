package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import kotlinx.serialization.SerialName
import com.google.android.gms.nearby.exposurenotification.ExposureInformation as NativeExposureInformation
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

@Serializable
data class ExposureInformation(
    @SerialName("AttenuationDurationsInMillis") val attenuationDurationsInMillis: IntArray,
    @SerialName("AttenuationValue") val attenuationValue: Int,
    @SerialName("DateMillisSinceEpoch") val dateMillisSinceEpoch: Long,
    @SerialName("DurationInMillis") val durationInMillis: Double,
    @SerialName("TotalRiskScore") val totalRiskScore: Int,
    @SerialName("TransmissionRiskLevel") val transmissionRiskLevel: Int,
) {
    companion object {
        private const val ONE_MINUTE_IN_MILLIS = 1000 * 60

        private fun convertToMillis(array: IntArray): IntArray =
            array.map { it * ONE_MINUTE_IN_MILLIS }.toIntArray()
    }

    constructor(exposureInformation: NativeExposureInformation) : this(
        attenuationDurationsInMillis = convertToMillis(exposureInformation.attenuationDurationsInMinutes),
        attenuationValue = exposureInformation.attenuationValue,
        dateMillisSinceEpoch = exposureInformation.dateMillisSinceEpoch,
        durationInMillis = (exposureInformation.durationMinutes * ONE_MINUTE_IN_MILLIS).toDouble(),
        totalRiskScore = exposureInformation.totalRiskScore,
        transmissionRiskLevel = exposureInformation.transmissionRiskLevel
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExposureInformation

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
