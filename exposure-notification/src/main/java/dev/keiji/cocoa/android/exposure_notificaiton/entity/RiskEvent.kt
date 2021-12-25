package dev.keiji.cocoa.android.exposure_notificaiton.entity

import java.util.*

data class RiskEvent(
    val date: Date,
    val riskLevel: RiskLevel,
    val count: Int
)

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
