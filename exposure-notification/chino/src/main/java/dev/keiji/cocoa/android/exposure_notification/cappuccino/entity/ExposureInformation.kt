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

}
