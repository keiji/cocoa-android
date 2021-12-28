package dev.keiji.cocoa.android.exposure_notification.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.toRFC3339Format
import dev.keiji.cocoa.android.exposure_notification.AnonymousInterceptorOkHttpClient
import dev.keiji.cocoa.android.exposure_notification.BuildConfig
import dev.keiji.cocoa.android.exposure_notification.core.entity.TemporaryExposureKey
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.http.PUT
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import retrofit2.http.Body
import java.util.*
import javax.inject.Singleton

interface DiagnosisSubmissionServiceApi {
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
