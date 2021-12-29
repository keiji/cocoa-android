package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import com.google.android.gms.nearby.exposurenotification.ExposureInformation as NativeExposureInformation
import kotlinx.serialization.Serializable

@Serializable
data class ExposureInformation(
    val attenuationDurationsInMillis: IntArray,
    val attenuationValue: Int,
    val dateMillisSinceEpoch: Long,
    val durationMinutes: Int,
    val totalRiskScore: Int,
    val transmissionRiskLevel: Int,
) {
    companion object {
        private fun convertToMillis(array: IntArray): IntArray =
            array.map { it * 60 * 1000 }.toIntArray()
    }

    constructor(exposureInformation: NativeExposureInformation) : this(
        attenuationDurationsInMillis = convertToMillis(exposureInformation.attenuationDurationsInMinutes),
        attenuationValue = exposureInformation.attenuationValue,
        dateMillisSinceEpoch = exposureInformation.dateMillisSinceEpoch,
        durationMinutes = exposureInformation.durationMinutes,
        totalRiskScore = exposureInformation.totalRiskScore,
        transmissionRiskLevel = exposureInformation.transmissionRiskLevel
    )

}
