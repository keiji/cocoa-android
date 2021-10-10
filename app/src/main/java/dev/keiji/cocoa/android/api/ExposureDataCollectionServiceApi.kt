package dev.keiji.cocoa.android.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.AnonymousInterceptorOkHttpClient
import dev.keiji.cocoa.android.BuildConfig
import dev.keiji.cocoa.android.entity.DailySummary
import dev.keiji.cocoa.android.entity.ExposureConfiguration
import dev.keiji.cocoa.android.entity.ExposureInformation
import dev.keiji.cocoa.android.entity.ExposureSummary
import dev.keiji.cocoa.android.entity.ExposureWindow
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.http.PUT
import retrofit2.http.Path

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import retrofit2.http.Body
import javax.inject.Singleton

interface ExposureDataCollectionServiceApi {
    @PUT("exposure_data/{clusterId}")
    suspend fun submit(
        @Path("clusterId") clusterId: String,
        @Body exposureDataRequest: ExposureDataRequest
    ): ExposureDataResponse
}

@Serializable
data class ExposureDataRequest constructor(
    @SerialName("device") val device: String,
    @SerialName("en_version") val enVersion: String,
    @SerialName("exposure_configuration") val exposureConfiguration: ExposureConfiguration,
    @SerialName("exposure_summary") val exposureSummary: ExposureSummary? = null,
    @SerialName("exposure_informations") val exposureInformationList: List<ExposureInformation>?,
    @SerialName("daily_summaries") val dailySummaryList: List<DailySummary>?,
    @SerialName("exposure_windows") val exposureWindowList: List<ExposureWindow>?,
)

@Serializable
data class ExposureDataResponse(
    @SerialName("device") val device: String,
    @SerialName("en_version") val enVersion: String,
    @SerialName("exposure_configuration") val exposureConfiguration: ExposureConfiguration,
    @SerialName("exposure_summary") val exposureSummary: ExposureSummary? = null,
    @SerialName("exposure_informations") val exposureInformationList: List<ExposureInformation>? = null,
    @SerialName("daily_summaries") val dailySummaries: List<DailySummary>?,
    @SerialName("exposure_windows") val exposureWindowList: List<ExposureWindow>? = null,
    @SerialName("file_name") val fileName: String,
    @SerialName("url") val url: String,
)

@Module
@InstallIn(SingletonComponent::class)
object ExposureDataCollectionServiceApiModule {

    @Singleton
    @Provides
    fun provideExposureDataCollectionServiceApi(
        @AnonymousInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): ExposureDataCollectionServiceApi {
        val contentType = MediaType.parse("application/json")!!

        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            useArrayPolymorphism = true
            coerceInputValues = false
        }
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.EXPOSURE_DATA_COLLECTION_SERVICE_API_ENDPOINT)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ExposureDataCollectionServiceApi::class.java)
    }
}
