package dev.keiji.cocoa.android.exposure_notification.exposure_detection

import android.content.Intent
import android.os.Build
import androidx.work.ListenableWorker
import dev.keiji.cocoa.android.exposure_notification.core.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataCollectionApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataRequest
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepository
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import timber.log.Timber

interface ExposureDetectionService {
    fun isExposureNotificationEnabled(intent: Intent): Boolean

    suspend fun noExposureDetectedWork(): ListenableWorker.Result;
    suspend fun v1ExposureDetectedWork(token: String): ListenableWorker.Result;
    suspend fun v2ExposureDetectedWork(): ListenableWorker.Result;
}

class ExposureDetectionServiceImpl(
    private val exposureConfigurationRepository: ExposureConfigurationRepository,
    private val exposureDataCollectionApi: ExposureDataCollectionApi,
    private val exposureNotificationWrapper: ExposureNotificationWrapper,
    private val configurationSource: ConfigurationSource,
) : ExposureDetectionService {

    override fun isExposureNotificationEnabled(intent: Intent): Boolean =
        intent.getBooleanExtra(ExposureNotificationWrapper.EXTRA_SERVICE_STATE, false)

    override suspend fun noExposureDetectedWork(): ListenableWorker.Result {
        try {
            val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()
            configurationSource.regions().forEach { region ->
                exposureDataCollectionApi.submit(
                    region,
                    ExposureDataRequest(
                        device = Build.MODEL,
                        enVersion = exposureNotificationWrapper.getVersion().toString(),
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

    override suspend fun v1ExposureDetectedWork(token: String): ListenableWorker.Result {
        try {
            val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()
            val exposureSummary = exposureNotificationWrapper.getExposureSummary(token)
            val exposureInformationList = exposureNotificationWrapper.getExposureInformation(token)

            configurationSource.regions().forEach { region ->
                exposureDataCollectionApi.submit(
                    region,
                    ExposureDataRequest(
                        device = Build.MODEL,
                        enVersion = exposureNotificationWrapper.getVersion().toString(),
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

    override suspend fun v2ExposureDetectedWork(): ListenableWorker.Result {
        try {
            val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()
            val dailySummary =
                exposureNotificationWrapper.getDailySummary(exposureConfiguration.dailySummaryConfig)
            val exposureWindowList =
                exposureNotificationWrapper.getExposureWindow()

            configurationSource.regions().forEach { region ->
                exposureDataCollectionApi.submit(
                    region,
                    ExposureDataRequest(
                        device = Build.MODEL,
                        enVersion = exposureNotificationWrapper.getVersion().toString(),
                        exposureConfiguration = exposureConfiguration,
                        exposureSummary = null,
                        exposureInformationList = null,
                        dailySummaryList = dailySummary,
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