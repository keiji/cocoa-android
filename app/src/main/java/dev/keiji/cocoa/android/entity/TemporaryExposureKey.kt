package dev.keiji.cocoa.android.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TemporaryExposureKey(

    @SerialName("key")
    val key: String,

    @SerialName("reportType")
    val reportType: Int,

    @SerialName("rollingStartNumber")
    val rollingStartNumber: Int,

    @SerialName("rollingPeriod")
    val rollingPeriod: Int,

    @SerialName("transmissionRisk")
    val transmissionRisk: Int = -1,

    @SerialName("daysSinceOnsetOfSymptoms")
    val daysSinceOnsetOfSymptoms: Int = -1,

    @SerialName("createdAt")
    val createdAt: Long = -1,
)
