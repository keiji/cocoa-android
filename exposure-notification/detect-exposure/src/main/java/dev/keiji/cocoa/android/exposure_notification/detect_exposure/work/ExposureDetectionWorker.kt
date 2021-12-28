package dev.keiji.cocoa.android.exposure_notification.detect_exposure.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.keiji.cocoa.android.exposure_notification.BuildConfig
import dev.keiji.cocoa.android.exposure_notification.entity.DiagnosisKeysFile
import dev.keiji.cocoa.android.exposure_notification.core.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.core.entity.ExposureNotificationStatus
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.repository.DiagnosisKeysFileRepository
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.repository.ExposureConfigurationRepository
import dev.keiji.cocoa.android.exposure_notification.regions
import dev.keiji.cocoa.android.exposure_notification.subregions
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

@HiltWorker
class ExposureDetectionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val diagnosisKeysFileRepository: DiagnosisKeysFileRepository,
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

                // Sub-region
                val subRegionDiagnosisKeyFiles = mutableListOf<DiagnosisKeysContainer>()
                subregions().forEach { subregion ->
                    subRegionDiagnosisKeyFiles.addAll(downloadDiagnosisKeys(region, subregion))
                }
                detectExposure(subRegionDiagnosisKeyFiles)

                // Region
                val diagnosisKeyFiles = downloadDiagnosisKeys(region, null)
                detectExposure(diagnosisKeyFiles)
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

    private suspend fun detectExposure(diagnosisKeys: List<DiagnosisKeysContainer>) {
        if (BuildConfig.USE_EXPOSURE_WINDOW_MODE) {
            detectExposureExposureWindowMode(diagnosisKeys)
        } else {
            detectExposureLegacyV1(diagnosisKeys)
        }

        diagnosisKeys.forEach { container ->
            container.file.delete()
        }
        diagnosisKeysFileRepository.setIsProcessed(
            diagnosisKeys.map { container -> container.diagnosisKeysFile }
        )
    }

    private suspend fun downloadDiagnosisKeys(
        region: String,
        subregion: String?
    ): List<DiagnosisKeysContainer> {
        val diagnosisKeyList =
            diagnosisKeysFileRepository.getDiagnosisKeysFileList(region, subregion)

        val diagnosisKeysContainers: MutableList<DiagnosisKeysContainer> = mutableListOf()

        diagnosisKeyList.forEach { diagnosisKeyEntry ->
            Timber.d(diagnosisKeyEntry.toString())
            val downloadedFile =
                diagnosisKeysFileRepository.getDiagnosisKeysFile(diagnosisKeyEntry)
                    ?: return@forEach

            diagnosisKeysContainers.add(
                DiagnosisKeysContainer(
                    diagnosisKeyEntry,
                    downloadedFile
                )
            )
        }

        return diagnosisKeysContainers
    }

    private suspend fun detectExposureExposureWindowMode(diagnosisKeys: List<DiagnosisKeysContainer>) {
        exposureNotificationWrapper.provideDiagnosisKeys(
            diagnosisKeys.map { container -> container.file }
        )
    }

    private suspend fun detectExposureLegacyV1(diagnosisKeys: List<DiagnosisKeysContainer>) {
        val exposureConfiguration =
            exposureConfigurationRepository.getExposureConfiguration(BuildConfig.EXPOSURE_CONFIGURATION_URL)
        Timber.d(exposureConfiguration.toString())

        val token = UUID.randomUUID().toString()

        exposureNotificationWrapper.provideDiagnosisKeys(
            diagnosisKeys.map { container -> container.file },
            exposureConfiguration.v1Config,
            token
        )
    }

    private data class DiagnosisKeysContainer(
        val diagnosisKeysFile: DiagnosisKeysFile,
        val file: File,
    )
}
