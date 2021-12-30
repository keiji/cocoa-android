package dev.keiji.cocoa.android.exposure_notification.diagnosis_submission.api

import retrofit2.http.PUT
import retrofit2.http.Body
import java.util.*

/**
 * @see https://github.com/keiji/en-calibration-server
 */

interface ENCalibrationSubmitDiagnosisApi : V3SubmitDiagnosisApi {
    @PUT("diagnosis_keys/diagnosis-keys.json")
    override suspend fun submitV3(
        @Body v3DiagnosisSubmissionRequest: V3DiagnosisSubmissionRequest
    ): List<V3DiagnosisSubmissionRequest.TemporaryExposureKey>
}
