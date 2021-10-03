package dev.keiji.cocoa.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import dev.keiji.cocoa.android.ExposureNotificationWrapper
import dev.keiji.cocoa.android.repository.ExposureConfigurationRepository
import timber.log.Timber

@AndroidEntryPoint
class ExposureDetectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.i("ExposureDetectionReceiver.onReceive")

        context ?: return
        intent ?: return

        val workManager = WorkManager.getInstance(context)

        when (intent.action) {
            ExposureNotificationClient.ACTION_EXPOSURE_NOT_FOUND -> {
                onNotDetectExposure(workManager)
            }
            ExposureNotificationClient.ACTION_EXPOSURE_STATE_UPDATED -> {
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

        val isV1Api = intent.hasExtra(ExposureNotificationClient.EXTRA_EXPOSURE_SUMMARY)

        if (isV1Api) {
            val token = intent.getStringExtra(ExposureNotificationClient.EXTRA_TOKEN) ?: return
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
        return success()
    }
}

@HiltWorker
class ExposureDetectionV1Worker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val exposureNotificationWrapper: ExposureNotificationWrapper
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
            val exposureSummary = exposureNotificationWrapper.getExposureSummary(token)
            val exposureInformationList = exposureNotificationWrapper.getExposureInformation(token)

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
    private val exposureNotificationWrapper: ExposureNotificationWrapper
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
            Timber.d(exposureConfiguration.toString())

            val dailySummary =
                exposureNotificationWrapper.getDailySummary(exposureConfiguration.dailySummaryConfig.toNative())
            val exposureWindowList = exposureNotificationWrapper.getExposureWindow()

        } catch (exception: Exception) {
            Timber.e(exception)
        }

        return success()
    }

}
