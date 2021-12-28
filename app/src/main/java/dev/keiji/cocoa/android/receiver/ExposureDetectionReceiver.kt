package dev.keiji.cocoa.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.*
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import dev.keiji.cocoa.android.exposure_notification.core.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataCollectionServiceApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataRequest
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepository
import timber.log.Timber

@AndroidEntryPoint
class ExposureDetectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.i("ExposureDetectionReceiver.onReceive")

        context ?: return
        intent ?: return

        val workManager = WorkManager.getInstance(context)

        when (intent.action) {
            ExposureNotificationWrapper.ACTION_EXPOSURE_NOT_FOUND -> {
                onNotDetectExposure(workManager)
            }
            ExposureNotificationWrapper.ACTION_EXPOSURE_STATE_UPDATED -> {
                onDetectExposure(workManager, intent)
            }
        }
    }

    private fun onNotDetectExposure(workManager: WorkManager) {
        Timber.i("No exposure detected.")

        NoExposureDetectionWorker.enqueue(
            workManager,
        )
    }

    private fun onDetectExposure(workManager: WorkManager, intent: Intent) {
        Timber.i("Exposure detected.")

        val isV1Api = intent.hasExtra(ExposureNotificationWrapper.EXTRA_EXPOSURE_SUMMARY)

        if (isV1Api) {
            val token = intent.getStringExtra(ExposureNotificationWrapper.EXTRA_TOKEN) ?: return
            ExposureDetectionV1Worker.enqueue(
                workManager,
                token
            )
        } else {
            ExposureDetectionV2Worker.enqueue(
                workManager,
            )
        }
    }
}

@HiltWorker
class NoExposureDetectionWorker @AssistedInject constructor(
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
            return OneTimeWorkRequestBuilder<NoExposureDetectionWorker>().build()
        }
    }

    override suspend fun doWork(): Result {
        try {
            val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()
            configurationSource.regions().forEach { region ->
                exposureDataCollectionServiceApi.submit(
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
        return success()
    }
}

@HiltWorker
class ExposureDetectionV1Worker @AssistedInject constructor(
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
            return OneTimeWorkRequestBuilder<ExposureDetectionV1Worker>()
                .setInputData(
                    workDataOf(
                        WORKER_PARAM_KEY_TOKEN to token
                    )
                ).build()
        }
    }

    override suspend fun doWork(): Result {
        val token = inputData.getString(WORKER_PARAM_KEY_TOKEN) ?: return failure()

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

        return success()
    }
}

@HiltWorker
class ExposureDetectionV2Worker @AssistedInject constructor(
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
            return OneTimeWorkRequest.from(ExposureDetectionV2Worker::class.java)
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

        return success()
    }

}
