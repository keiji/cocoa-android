package dev.keiji.cocoa.android.exposure_notification.exposure_detection.api

import dev.keiji.cocoa.android.exposure_notification.core.entity.DailySummary
import dev.keiji.cocoa.android.exposure_notification.core.entity.ExposureConfiguration
import dev.keiji.cocoa.android.exposure_notification.core.entity.ExposureInformation
import dev.keiji.cocoa.android.exposure_notification.core.entity.ExposureSummary
import dev.keiji.cocoa.android.exposure_notification.core.entity.ExposureWindow
import retrofit2.http.PUT
import retrofit2.http.Path

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body

interface ExposureDataCollectionServiceApi {
    @PUT("exposure_data/{region}")
    suspend fun submit(
        @Path("region") region: String,
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
