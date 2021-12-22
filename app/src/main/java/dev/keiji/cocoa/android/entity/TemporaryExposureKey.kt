package dev.keiji.cocoa.android.entity

import dev.keiji.util.Base64
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey as NativeTemporaryExposureKey

@Serializable
data class TemporaryExposureKey(

    @SerialName("keyData")
    val key: String,

    @SerialName("rollingStartNumber")
    val rollingStartNumber: Int,

    @SerialName("rollingPeriod")
    val rollingPeriod: Int,

    @SerialName("reportType")
    val reportType: Int,

    @SerialName("transmissionRisk")
    val transmissionRisk: Int = -1,

    @SerialName("daysSinceOnsetOfSymptoms")
    val daysSinceOnsetOfSymptoms: Int = -1,

    @SerialName("createdAt")
    val createdAt: Long = -1,
) {
    constructor(
        temporaryExposureKey: NativeTemporaryExposureKey,
        reportType: Int,
    ) : this(
        key = Base64.encode(temporaryExposureKey.keyData),
        rollingStartNumber = temporaryExposureKey.rollingStartIntervalNumber,
        rollingPeriod = temporaryExposureKey.rollingPeriod,
        reportType = reportType,
    )
}
