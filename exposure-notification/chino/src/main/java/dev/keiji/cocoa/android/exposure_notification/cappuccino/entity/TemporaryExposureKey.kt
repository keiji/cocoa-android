package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import dev.keiji.util.Base64
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.random.Random

import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey as NativeTemporaryExposureKey

@Serializable
data class TemporaryExposureKey(

    @SerialName("keyData")
    val key: String,

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
) {
    companion object {
        private const val KEY_LENGTH = 16
        private const val ROLLING_PERIOD = 144

        fun createDummy(random: Random, rollingStartNumber: Int): TemporaryExposureKey {
            val keyBytes = random.nextBytes(KEY_LENGTH)
            return TemporaryExposureKey(
                key = Base64.encode(keyBytes),
                rollingStartNumber = rollingStartNumber,
                rollingPeriod = 10,
            )
        }
    }

    @SerialName("reportType")
    var reportType: Int = ReportType.UNKNOWN.ordinal

    constructor(
        temporaryExposureKey: NativeTemporaryExposureKey
    ) : this(
        key = Base64.encode(temporaryExposureKey.keyData),
        rollingStartNumber = temporaryExposureKey.rollingStartIntervalNumber,
        rollingPeriod = temporaryExposureKey.rollingPeriod,
    )
}
