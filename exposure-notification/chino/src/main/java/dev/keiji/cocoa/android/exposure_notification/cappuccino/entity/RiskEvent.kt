package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import org.joda.time.DateTime

data class RiskEvent(
    val dateTime: DateTime,
    var legacyV1RiskLevel: RiskLevel = RiskLevel.RISK_LEVEL_INVALID,
    var legacyV1Count: Int = 0,
    var exposureWIndowRiskLevel: RiskLevel = RiskLevel.RISK_LEVEL_INVALID,
    var exposureInSeconds: Int = 0,
) : Comparable<RiskEvent> {

    override fun compareTo(other: RiskEvent): Int {
        val dateTimeCompared = dateTime.compareTo(other.dateTime)
        return when {
            dateTimeCompared != 0 -> dateTimeCompared
            exposureWIndowRiskLevel > other.exposureWIndowRiskLevel -> 1
            exposureWIndowRiskLevel < other.exposureWIndowRiskLevel -> -1
            legacyV1RiskLevel > other.legacyV1RiskLevel -> 1
            legacyV1RiskLevel < other.legacyV1RiskLevel -> -1
            exposureInSeconds > other.exposureInSeconds -> 1
            exposureInSeconds < other.exposureInSeconds -> -1
            legacyV1Count > other.legacyV1Count -> 1
            legacyV1Count < other.legacyV1Count -> -1
            else -> 0
        }
    }
}

enum class RiskLevel {
    RISK_LEVEL_INVALID,
    RISK_LEVEL_LOWEST,
    RISK_LEVEL_LOW,
    RISK_LEVEL_LOW_MEDIUM,
    RISK_LEVEL_MEDIUM,
    RISK_LEVEL_MEDIUM_HIGH,
    RISK_LEVEL_HIGH,
    RISK_LEVEL_VERY_HIGH,
    RISK_LEVEL_HIGHEST,
}
