package dev.keiji.cocoa.android.work

import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.keiji.cocoa.android.exposure_notification.core.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataCollectionServiceApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataRequest
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepository
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import timber.log.Timber

@HiltWorker
class V1ExposureDetectionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val exposureConfigurationRepository: ExposureConfigurationRepository,
    private val exposureDataCollectionServiceApi: ExposureDataCollectionServiceApi,
    private val exposureNotificationWrapper: ExposureNotificationWrapper,
    private val configurationSource: ConfigurationSource,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val WORKER_PARAM_KEY_TOKEN = "worker_param_key_token"

        fun enqueue(
            workManager: WorkManager,
            token: String,
        ): Operation = workManager.enqueue(createWorkerRequest(token))

        private fun createWorkerRequest(token: String): WorkRequest {
            return OneTimeWorkRequestBuilder<V1ExposureDetectionWorker>()
                .setInputData(
                    workDataOf(
                        WORKER_PARAM_KEY_TOKEN to token
                    )
                ).build()
        }
    }

    override suspend fun doWork(): Result {
        val token = inputData.getString(WORKER_PARAM_KEY_TOKEN) ?: return Result.failure()

        try {
            val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()
            val exposureSummary = exposureNotificationWrapper.getExposureSummary(token)
            val exposureInformationList = exposureNotificationWrapper.getExposureInformation(token)

            configurationSource.regions().forEach { region ->
                exposureDataCollectionServiceApi.submit(
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

        return Result.success()
    }
}
