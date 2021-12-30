package dev.keiji.cocoa.android.exposure_notification.exposure_detection

import android.content.Intent
import android.os.Build
import androidx.work.ListenableWorker
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.DailySummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureConfiguration
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureInformation
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureSummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureWindow
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataCollectionApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataRequest
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import timber.log.Timber

interface ExposureDetectionService {
    fun isExposureNotificationEnabled(intent: Intent): Boolean

    suspend fun noExposureDetectedWork(
        enVersion: Long,
        exposureConfiguration: ExposureConfiguration,
    ): ListenableWorker.Result;

    suspend fun v1ExposureDetectedWork(
        enVersion: Long,
        exposureSummary: ExposureSummary,
        exposureInformationList: List<ExposureInformation>,
        exposureConfiguration: ExposureConfiguration,
    ): ListenableWorker.Result;

    suspend fun v2ExposureDetectedWork(
        enVersion: Long,
        dailySummaryList: List<DailySummary>,
        exposureWindowList: List<ExposureWindow>,
        exposureConfiguration: ExposureConfiguration,
    ): ListenableWorker.Result;
}

class ExposureDetectionServiceImpl(
    private val exposureDataCollectionApi: ExposureDataCollectionApi,
    private val configurationSource: ConfigurationSource,
) : ExposureDetectionService {

    override fun isExposureNotificationEnabled(intent: Intent): Boolean =
        intent.getBooleanExtra(ExposureNotificationWrapper.EXTRA_SERVICE_STATE, false)

    override suspend fun noExposureDetectedWork(
        enVersion: Long,
        exposureConfiguration: ExposureConfiguration,
    ): ListenableWorker.Result {
        try {
            configurationSource.regions().forEach { region ->
                exposureDataCollectionApi.submit(
                    region,
                    ExposureDataRequest(
                        device = Build.MODEL,
                        enVersion = enVersion.toString(),
                        exposureConfiguration = exposureConfiguration,
                        null,
                        null,
                        null,
                        null
                    )
                )
            }
        } catch (exception: Exception) {
            Timber.e(exception)
        }
        return ListenableWorker.Result.success()
    }

    override suspend fun v1ExposureDetectedWork(
        enVersion: Long,
        exposureSummary: ExposureSummary,
        exposureInformationList: List<ExposureInformation>,
        exposureConfiguration: ExposureConfiguration,
    ): ListenableWorker.Result {
        try {
            configurationSource.regions().forEach { region ->
                exposureDataCollectionApi.submit(
                    region,
                    ExposureDataRequest(
                        device = Build.MODEL,
                        enVersion = enVersion.toString(),
                        exposureConfiguration = exposureConfiguration,
                        exposureSummary = exposureSummary,
                        exposureInformationList = exposureInformationList,
                        dailySummaryList = null,
                        exposureWindowList = null
                    )
                )
            }
        } catch (exception: Exception) {
            Timber.e(exception)
        }

        return ListenableWorker.Result.success()
    }

    override suspend fun v2ExposureDetectedWork(
        enVersion: Long,
        dailySummaryList: List<DailySummary>,
        exposureWindowList: List<ExposureWindow>,
        exposureConfiguration: ExposureConfiguration,
    ): ListenableWorker.Result {
        try {
            configurationSource.regions().forEach { region ->
                exposureDataCollectionApi.submit(
                    region,
                    ExposureDataRequest(
                        device = Build.MODEL,
                        enVersion = enVersion.toString(),
                        exposureConfiguration = exposureConfiguration,
                        exposureSummary = null,
                        exposureInformationList = null,
                        dailySummaryList = dailySummaryList,
                        exposureWindowList = exposureWindowList
                    )
                )
            }
        } catch (exception: Exception) {
            Timber.e(exception)
        }

        return ListenableWorker.Result.success()
    }
}