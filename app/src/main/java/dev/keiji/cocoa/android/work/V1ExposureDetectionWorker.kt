package dev.keiji.cocoa.android.work

import android.content.Context
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
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.ExposureDetectionService
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepository

@HiltWorker
class V1ExposureDetectionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val exposureConfigurationRepository: ExposureConfigurationRepository,
    private val exposureDetectionService: ExposureDetectionService,
    private val exposureNotificationWrapper: ExposureNotificationWrapper,
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

        val enVersion = exposureNotificationWrapper.getVersion()
        val exposureSummary = exposureNotificationWrapper.getExposureSummary(token)
        val exposureInformationList = exposureNotificationWrapper.getExposureInformation(token)
        val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()

        return exposureDetectionService.v1ExposureDetectedWork(
            enVersion,
            exposureSummary,
            exposureInformationList,
            exposureConfiguration,
        )
    }
}
