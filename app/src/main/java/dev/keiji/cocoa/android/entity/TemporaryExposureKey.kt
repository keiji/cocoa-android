package dev.keiji.cocoa.android.entity

import com.google.android.gms.nearby.exposurenotification.ReportType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TemporaryExposureKey(

    @SerialName("key")
    val key: String,

    @SerialName("rollingStartNumber")
    val rollingStartNumber: Int,

    @SerialName("rollingPeriod")
    val rollingPeriod: Int,

    @SerialName("transmissionRisk")
    val transmissionRisk: Int = -1,

    @SerialName("reportType")
    val reportType: Int = ReportType.UNKNOWN,

    @SerialName("daysSinceOnsetOfSymptoms")
    val daysSinceOnsetOfSymptoms: Int = -1,

    @SerialName("createdAt")
    val createdAt: Long = -1,
)
