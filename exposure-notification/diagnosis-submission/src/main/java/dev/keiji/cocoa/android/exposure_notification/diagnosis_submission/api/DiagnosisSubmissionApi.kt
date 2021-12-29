package dev.keiji.cocoa.android.exposure_notification.diagnosis_submission.api

import dev.keiji.cocoa.android.exposure_notification.toRFC3339Format
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.TemporaryExposureKey
import retrofit2.http.PUT
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import java.util.*

interface SubmitDiagnosisServiceApi {
    @PUT("diagnosis_keys/diagnosis-keys.json")
    suspend fun submitV3(
        @Body diagnosisSubmissionRequest: DiagnosisSubmissionRequest
    ): List<TemporaryExposureKey?>
}

@Serializable
data class DiagnosisSubmissionRequest constructor(
    @SerialName("idempotencyKey") val idempotencyKey: String,
    @SerialName("regions") val regions: List<String>,
    @SerialName("sub_regions") val subRegions: List<String>,
    @SerialName("symptomOnsetDate") val symptomOnsetDate: String,
    @SerialName("keys") val temporaryExposureKeys: List<TemporaryExposureKey>
) {
    constructor(
        idempotencyKey: String,
        regions: List<String>,
        subRegions: List<String>,
        symptomOnsetDate: Date,
        temporaryExposureKeys: List<TemporaryExposureKey>
    ) : this(
        idempotencyKey,
        regions,
        subRegions,
        symptomOnsetDate.toRFC3339Format(),
        temporaryExposureKeys
    )
}
