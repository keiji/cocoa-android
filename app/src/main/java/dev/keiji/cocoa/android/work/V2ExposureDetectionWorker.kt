package dev.keiji.cocoa.android.work

import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.keiji.cocoa.android.exposure_notification.core.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataCollectionServiceApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataRequest
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepository
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import timber.log.Timber

@HiltWorker
class V2ExposureDetectionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val exposureConfigurationRepository: ExposureConfigurationRepository,
    private val exposureDataCollectionServiceApi: ExposureDataCollectionServiceApi,
    private val exposureNotificationWrapper: ExposureNotificationWrapper,
    private val configurationSource: ConfigurationSource,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        fun enqueue(
            workManager: WorkManager,
        ): Operation = workManager.enqueue(createWorkerRequest())

        private fun createWorkerRequest(): WorkRequest {
            return OneTimeWorkRequest.from(V2ExposureDetectionWorker::class.java)
        }
    }

    override suspend fun doWork(): Result {
        try {
            val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()
            val dailySummary =
                exposureNotificationWrapper.getDailySummary(exposureConfiguration.dailySummaryConfig)
            val exposureWindowList =
                exposureNotificationWrapper.getExposureWindow()

            configurationSource.regions().forEach { region ->
                exposureDataCollectionServiceApi.submit(
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

        return Result.success()
    }

}
