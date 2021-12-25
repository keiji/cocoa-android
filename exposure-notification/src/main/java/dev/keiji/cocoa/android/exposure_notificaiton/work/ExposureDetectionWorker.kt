package dev.keiji.cocoa.android.exposure_notificaiton.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.keiji.cocoa.android.ExposureNotificationWrapper
import dev.keiji.cocoa.android.api.DiagnosisKeyFileProvideServiceApi
import dev.keiji.cocoa.android.api.DiagnosisKeyListProvideServiceApi
import dev.keiji.cocoa.android.exposure_notificaiton.BuildConfig
import dev.keiji.cocoa.android.regions
import dev.keiji.cocoa.android.repository.ExposureConfigurationRepository
import dev.keiji.cocoa.android.subregions
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

        try {
            regions().forEach { region ->
                val diagnosisKeyFiles = downloadDiagnosisKeys(region, null)
                detectExposure(diagnosisKeyFiles)

                val subRegionDiagnosisKeyFiles = mutableListOf<File>()
                subregions().forEach { subregion ->
                    subRegionDiagnosisKeyFiles.addAll(downloadDiagnosisKeys(region, subregion))
                }
                detectExposure(subRegionDiagnosisKeyFiles)
            }
            return Result.success();
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

    private suspend fun detectExposure(diagnosisKeyFiles: List<File>) {
        if (BuildConfig.USE_EXPOSURE_WINDOW_MODE) {
            detectExposureExposureWindowMode(diagnosisKeyFiles)
        } else {
            detectExposureLegacyV1(diagnosisKeyFiles)
        }

        diagnosisKeyFiles.forEach { file ->
            file.delete()
        }
    }

    private suspend fun downloadDiagnosisKeys(region: String, subregion: String?): List<File> {
        var outputDir = File(File(applicationContext.filesDir, DIR_NAME), region)

        val diagnosisKeyList = if (subregion != null) {
            outputDir = File(outputDir, subregion)
            diagnosisKeyListProvideServiceApi.getList(region, subregion)
        } else {
            diagnosisKeyListProvideServiceApi.getList(region)
        }

        val downloadedFiles = diagnosisKeyList.map { diagnosisKeyEntry ->
            Timber.d(diagnosisKeyEntry.toString())
            diagnosisKeyEntry ?: return@map null

            return@map diagnosisKeyFileProvideServiceApi.getFile(diagnosisKeyEntry, outputDir)
        }.filterNotNull()

        return downloadedFiles
    }

    private suspend fun detectExposureExposureWindowMode(downloadedFiles: List<File>) {
        exposureNotificationWrapper.provideDiagnosisKeys(downloadedFiles)
    }

    private suspend fun detectExposureLegacyV1(downloadedFiles: List<File>) {
        val exposureConfiguration =
            exposureConfigurationRepository.getExposureConfiguration(BuildConfig.EXPOSURE_CONFIGURATION_URL)
        Timber.d(exposureConfiguration.toString())

        val token = UUID.randomUUID().toString()

        exposureNotificationWrapper.provideDiagnosisKeys(
            downloadedFiles,
            exposureConfiguration.v1Config.toNative(),
            token
        )
    }
}
