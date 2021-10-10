package dev.keiji.cocoa.android.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.keiji.cocoa.android.BuildConfig
import dev.keiji.cocoa.android.ExposureNotificationWrapper
import dev.keiji.cocoa.android.api.DiagnosisKeyFileProvideServiceApi
import dev.keiji.cocoa.android.api.DiagnosisKeyListProvideServiceApi
import dev.keiji.cocoa.android.repository.ExposureConfigurationRepository
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

@HiltWorker
class ExposureDetectionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val diagnosisKeyListProvideServiceApi: DiagnosisKeyListProvideServiceApi,
    private val diagnosisKeyFileProvideServiceApi: DiagnosisKeyFileProvideServiceApi,
    private val exposureConfigurationRepository: ExposureConfigurationRepository,
    private val exposureNotificationWrapper: ExposureNotificationWrapper,
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        private const val DIR_NAME = "diagnosis_keys"
    }

    override suspend fun doWork(): Result {
        Timber.d("Starting worker...")

        if (!exposureNotificationWrapper.isEnabled()) {
            Timber.w("ExposureNotification is disabled.")
            return Result.failure()
        }

        if (!exposureNotificationWrapper.getStatuses()
                .any { it == ExposureNotificationStatus.ACTIVATED }
        ) {
            Timber.w("ExposureNotification is not activated.")
            return Result.failure()
        }

        val outputDir = File(File(applicationContext.filesDir, DIR_NAME), BuildConfig.CLUSTER_ID)

        try {
            val diagnosisKeyList = diagnosisKeyListProvideServiceApi.getList(BuildConfig.CLUSTER_ID)
            val downloadedFiles = diagnosisKeyList.map { diagnosisKeyEntry ->
                Timber.d(diagnosisKeyEntry.toString())
                diagnosisKeyEntry ?: return@map null

                return@map diagnosisKeyFileProvideServiceApi.getFile(diagnosisKeyEntry, outputDir)
            }.filterNotNull()

            if (BuildConfig.USE_EXPOSURE_WINDOW_MODE) {
                detectExposureExposureWindowMode(downloadedFiles)
            } else {
                detectExposureLegacyV1(downloadedFiles)
            }

            downloadedFiles.forEach { file ->
                file.delete()
            }

            return Result.success()
        } catch (e: IOException) {
            Timber.e(e.javaClass.simpleName, e)
            return Result.retry()
        } catch (e: Exception) {
            Timber.e(e.javaClass.simpleName, e)
            return Result.failure()
        } finally {
            Timber.d("Starting finished.")
        }
    }

    private suspend fun detectExposureExposureWindowMode(downloadedFiles: List<File>) {
        exposureNotificationWrapper.provideDiagnosisKeys(downloadedFiles)
    }

    private suspend fun detectExposureLegacyV1(downloadedFiles: List<File>) {
        val exposureConfiguration =
            exposureConfigurationRepository.getExposureConfiguration()
        Timber.d(exposureConfiguration.toString())

        val token = UUID.randomUUID().toString()

        exposureNotificationWrapper.provideDiagnosisKeys(
            downloadedFiles,
            exposureConfiguration.v1Config.toNative(),
            token
        )
    }
}
