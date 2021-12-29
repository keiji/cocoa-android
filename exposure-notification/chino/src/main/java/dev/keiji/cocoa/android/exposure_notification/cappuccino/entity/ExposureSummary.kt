package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.google.android.gms.nearby.exposurenotification.ExposureSummary as NativeExposureSummary

@Serializable
data class ExposureSummary(
    @SerialName("AttenuationDurationsInMinutes") val attenuationDurationsInMinutes: IntArray,
    @SerialName("DaysSinceLastExposure") val daysSinceLastExposure: Int,
    @SerialName("MatchedKeyCount") val matchedKeyCount: Int,
    @SerialName("MaximumRiskScore") val maximumRiskScore: Int,
    @SerialName("SummationRiskScore") val summationRiskScore: Int,
) {
    constructor(exposureSummary: NativeExposureSummary) : this(
        attenuationDurationsInMinutes = exposureSummary.attenuationDurationsInMinutes,
        daysSinceLastExposure = exposureSummary.daysSinceLastExposure,
        matchedKeyCount = exposureSummary.matchedKeyCount,
        maximumRiskScore = exposureSummary.maximumRiskScore,
        summationRiskScore = exposureSummary.summationRiskScore,
    )
}
