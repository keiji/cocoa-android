package dev.keiji.cocoa.android.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.AnonymousInterceptorOkHttpClient
import dev.keiji.cocoa.android.BuildConfig
import dev.keiji.cocoa.android.DefaultInterceptorOkHttpClient
import dev.keiji.cocoa.android.entity.TemporaryExposureKey
import dev.keiji.cocoa.android.toRFC3339Format
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.http.PUT
import retrofit2.http.Path

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import retrofit2.http.Body
import java.util.*
import javax.inject.Singleton

interface DiagnosisSubmissionServiceApi {
    @PUT("diagnosis_keys/{clusterId}/diagnosis-keys.json")
    suspend fun submitV3(
        @Path("clusterId") clusterId: String,
        @Body diagnosisSubmissionRequest: DiagnosisSubmissionRequest
    ): List<TemporaryExposureKey?>
}

@Serializable
data class DiagnosisSubmissionRequest constructor(
    @SerialName("idempotencyKey") val idempotencyKey: String,
    @SerialName("symptomOnsetDate") val symptomOnsetDate: String,
    @SerialName("temporaryExposureKeys") val temporaryExposureKeys: List<TemporaryExposureKey>
) {
    constructor(
        idempotencyKey: String,
        symptomOnsetDate: Date,
        temporaryExposureKeys: List<TemporaryExposureKey>
    ) : this(idempotencyKey, symptomOnsetDate.toRFC3339Format(), temporaryExposureKeys)
}

@Module
@InstallIn(SingletonComponent::class)
object DiagnosisSubmissionServiceApiModule {

    @Singleton
    @Provides
    fun provideDiagnosisSubmissionServiceApi(
        @AnonymousInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): DiagnosisSubmissionServiceApi {
        val contentType = MediaType.parse("application/json")!!

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.DIAGNOSIS_SUBMISSION_API_ENDPOINT)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(DiagnosisSubmissionServiceApi::class.java)
    }
}