package dev.keiji.cocoa.android.exposure_notification.diagnosis_submission.api

import dev.keiji.cocoa.android.common.attestation.AttestationRequest
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.TemporaryExposureKey as ChinoTemporaryExposureKey
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ReportType
import dev.keiji.cocoa.android.exposure_notification.toRFC3339Format
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.joda.time.DateTime
import retrofit2.http.Body
import retrofit2.http.PUT
import java.util.*

interface V3SubmitDiagnosisApi {
    @PUT("v3/diagnosis")
    suspend fun submitV3(
        @Body v3DiagnosisSubmissionRequest: V3DiagnosisSubmissionRequest
    ): List<V3DiagnosisSubmissionRequest.TemporaryExposureKey>
}

@Serializable
data class V3DiagnosisSubmissionRequest constructor(
    @SerialName("idempotencyKey") val idempotencyKey: String,
    @SerialName("regions") val regions: List<Int>,
    @SerialName("subRegions") val subRegions: List<String>,
    @SerialName("symptomOnsetDate") val symptomOnsetDate: String,
    @SerialName("keys") val temporaryExposureKeys: List<TemporaryExposureKey>,
    @SerialName("appPackageName") val appPackageName: String? = null,
    @SerialName("verificationPayload") val processNumber: String? = null,
    @SerialName("deviceVerificationPayload") var jwsPayload: String? = null,
) : AttestationRequest {
    companion object {
        const val MIN_PADDING_SIZE = 1024
        const val MAX_PADDING_SIZE = 2048

        private const val PADDING_CHAR_RANGE = ('~'.code - '!'.code)

        private fun createPadding(): String {
            val random = Random()
            val padSize = MIN_PADDING_SIZE + random.nextInt(MAX_PADDING_SIZE - MIN_PADDING_SIZE)

            val padChars = CharArray(padSize) {
                ('!'.code + random.nextInt(PADDING_CHAR_RANGE)).toChar()
            }

            return String(padChars)
        }
    }

    @SerialName("platform")
    val platform: String = "android"

    @SerialName("padding")
    val padding: String = createPadding()

    constructor(
        idempotencyKey: String,
        regions: List<Int>,
        subRegions: List<String>,
        symptomOnsetDate: DateTime,
        temporaryExposureKeys: List<TemporaryExposureKey>,
        processNumber: String? = null,
        appPackageName: String? = null,
        jwsPayload: String? = null,
    ) : this(
        idempotencyKey = idempotencyKey,
        regions = regions,
        subRegions = subRegions,
        symptomOnsetDate = symptomOnsetDate.toRFC3339Format(),
        temporaryExposureKeys = temporaryExposureKeys,
        processNumber = processNumber,
        appPackageName = appPackageName,
        jwsPayload = jwsPayload
    )

    private val keysClearText = temporaryExposureKeys.map { temporaryExposureKey ->
        temporaryExposureKey.getClearText()
    }.joinToString(",")

    private val regionsCleaText = regions
        .sorted()
        .joinToString(",")

    override fun getClearText(): String = arrayOf(
        symptomOnsetDate,
        appPackageName,
        keysClearText,
        regionsCleaText,
        processNumber,
    ).joinToString("|")

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

        @SerialName("reportType")
        val reportType: Int = ReportType.UNKNOWN.ordinal
    ) : AttestationRequest {
        @SerialName("createdAt")
        val createdAt: Long = -1

        constructor(temporaryExposureKey: ChinoTemporaryExposureKey, reportType: Int) : this(
            temporaryExposureKey.key,
            temporaryExposureKey.rollingStartNumber,
            temporaryExposureKey.rollingPeriod,
            temporaryExposureKey.transmissionRisk,
            temporaryExposureKey.daysSinceOnsetOfSymptoms,
            reportType,
        )

        override fun getClearText(): String =
            arrayOf(
                key,
                rollingStartNumber,
                rollingPeriod,
                reportType
            ).joinToString(".")
    }
}
