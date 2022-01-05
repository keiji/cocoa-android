package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import org.joda.time.DateTime

data class RiskEvent(
    val dateTime: DateTime,
    val riskLevel: RiskLevel,
    val count: Int
) : Comparable<RiskEvent> {
    override fun compareTo(other: RiskEvent): Int {
        val dateTimeCompared = dateTime.compareTo(other.dateTime)
        return when {
            dateTimeCompared != 0 -> dateTimeCompared
            riskLevel > other.riskLevel -> 1
            riskLevel < other.riskLevel -> -1
            count > other.count -> 1
            count < other.count -> -1
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
