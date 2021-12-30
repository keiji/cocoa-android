package dev.keiji.cocoa.android.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.ExposureDetectionService
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepository

@HiltWorker
class V2ExposureDetectionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val exposureConfigurationRepository: ExposureConfigurationRepository,
    private val exposureDetectionService: ExposureDetectionService,
    private val exposureNotificationWrapper: ExposureNotificationWrapper,
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
        val enVersion = exposureNotificationWrapper.getVersion()
        val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()

        val dailySummaryList =
            exposureNotificationWrapper.getDailySummary(exposureConfiguration.dailySummaryConfig)
        val exposureWindowList = exposureNotificationWrapper.getExposureWindow()

        return exposureDetectionService.v2ExposureDetectedWork(
            enVersion,
            dailySummaryList,
            exposureWindowList,
            exposureConfiguration
        )
    }

}
