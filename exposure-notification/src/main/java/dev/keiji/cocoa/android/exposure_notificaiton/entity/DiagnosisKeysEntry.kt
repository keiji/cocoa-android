package dev.keiji.cocoa.android.exposure_notificaiton.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiagnosisKeysEntry constructor(
    @SerialName("region") val region: Int,
    @SerialName("url") val url: String,
    @SerialName("created") val created: Long,
)
