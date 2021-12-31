package dev.keiji.cocoa.android.exposure_notification.exposure_detection

import android.content.Intent
import android.os.Build
import androidx.work.ListenableWorker
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataCollectionApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataRequest
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepository
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import timber.log.Timber

interface ExposureDetectionService {
    fun isExposureNotificationEnabled(intent: Intent): Boolean

    suspend fun noExposureDetectedWork(
        exposureNotificationWrapper: ExposureNotificationWrapper,
    ): ListenableWorker.Result;

    suspend fun v1ExposureDetectedWork(
        token: String,
        exposureNotificationWrapper: ExposureNotificationWrapper,
    ): ListenableWorker.Result;

    suspend fun v2ExposureDetectedWork(
        exposureNotificationWrapper: ExposureNotificationWrapper,
    ): ListenableWorker.Result;
}

class ExposureDetectionServiceImpl(
    private val exposureConfigurationRepository: ExposureConfigurationRepository,
    private val exposureDataCollectionApi: ExposureDataCollectionApi,
    private val configurationSource: ConfigurationSource,
) : ExposureDetectionService {

    override fun isExposureNotificationEnabled(intent: Intent): Boolean =
        intent.getBooleanExtra(ExposureNotificationWrapper.EXTRA_SERVICE_STATE, false)

    override suspend fun noExposureDetectedWork(
        exposureNotificationWrapper: ExposureNotificationWrapper,
    ): ListenableWorker.Result {
        val enVersion = exposureNotificationWrapper.getVersion()
        val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()

        try {
            configurationSource.regions.forEach { region ->
                exposureDataCollectionApi.submit(
                    region.toString(),
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
        token: String,
        exposureNotificationWrapper: ExposureNotificationWrapper,
    ): ListenableWorker.Result {
        val enVersion = exposureNotificationWrapper.getVersion()
        val exposureSummary = exposureNotificationWrapper.getExposureSummary(token)
        val exposureInformationList = exposureNotificationWrapper.getExposureInformation(token)
        val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()

        try {
            configurationSource.regions.forEach { region ->
                exposureDataCollectionApi.submit(
                    region.toString(),
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
        exposureNotificationWrapper: ExposureNotificationWrapper,
    ): ListenableWorker.Result {
        val enVersion = exposureNotificationWrapper.getVersion()
        val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()

        val dailySummaryList =
            exposureNotificationWrapper.getDailySummary(exposureConfiguration.dailySummaryConfig)
        val exposureWindowList = exposureNotificationWrapper.getExposureWindow()

        try {
            configurationSource.regions.forEach { region ->
                exposureDataCollectionApi.submit(
                    region.toString(),
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