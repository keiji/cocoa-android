package dev.keiji.cocoa.android.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.model.DiagnosisKeysFileModel
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureNotificationStatus
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.DiagnosisKeysFileRepository
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepository
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataBaseModel
import dev.keiji.cocoa.android.exposure_notification.model.State
import dev.keiji.cocoa.android.exposure_notification.repository.ExposureDataRepository
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

@HiltWorker
class DetectExposureWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dateTimeSource: DateTimeSource,
    private val diagnosisKeysFileRepository: DiagnosisKeysFileRepository,
    private val exposureConfigurationRepository: ExposureConfigurationRepository,
    private val exposureDataRepository: ExposureDataRepository,
    private val exposureNotificationWrapper: ExposureNotificationWrapper,
    private val configurationSource: ConfigurationSource,
) : CoroutineWorker(appContext, workerParams) {

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
            configurationSource.regions.forEach { region ->
                // Sub-region
                val subregions = mutableListOf<String>()
                val subRegionDiagnosisKeyFiles = mutableListOf<DiagnosisKeysContainer>()
                configurationSource.subregions.forEach { subregion ->
                    val list = downloadDiagnosisKeys(region.toString(), subregion)
                    if (list.isNotEmpty()) {
                        subregions.add(subregion)
                        subRegionDiagnosisKeyFiles.addAll(list)
                    }
                }
                detectExposure(
                    region = region.toString(),
                    subregionList = subregions,
                    diagnosisKeysFileContainerList = subRegionDiagnosisKeyFiles,
                )

                // Region
                val diagnosisKeyFiles = downloadDiagnosisKeys(
                    region.toString(),
                    null
                )
                detectExposure(
                    region = region.toString(),
                    diagnosisKeysFileContainerList = diagnosisKeyFiles,
                )
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

    private suspend fun saveExposureData(
        region: String,
        subregionList: List<String> = emptyList(),
        diagnosisKeysFileList: List<DiagnosisKeysFileModel>
    ) {
        val exposureDataBaseModel = ExposureDataBaseModel(
            id = 0,
            region = region,
            subregionList = subregionList,
            enVersion = exposureNotificationWrapper.getVersion().toString(),
            startEpoch = dateTimeSource.epoch(),
        )
        exposureDataRepository.save(
            exposureDataBaseModel,
            diagnosisKeysFileList = diagnosisKeysFileList,
        )
        diagnosisKeysFileRepository.setState(
            diagnosisKeysFileList,
            State.Processing
        )
    }

    private suspend fun detectExposure(
        region: String,
        subregionList: List<String> = emptyList(),
        diagnosisKeysFileContainerList: List<DiagnosisKeysContainer>
    ) {
        saveExposureData(
            region,
            subregionList,
            diagnosisKeysFileContainerList.map { container -> container.diagnosisKeysFileModel },
        )

        if (configurationSource.isEnabledExposureWindowMode()) {
            detectExposureExposureWindowMode(diagnosisKeysFileContainerList)
        } else {
            detectExposureLegacyV1(diagnosisKeysFileContainerList)
        }

        diagnosisKeysFileContainerList.forEach { container ->
            container.file.delete()
        }
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
            exposureConfigurationRepository.getExposureConfiguration()
        Timber.d(exposureConfiguration.toString())

        val token = UUID.randomUUID().toString()

        exposureNotificationWrapper.provideDiagnosisKeys(
            diagnosisKeys.map { container -> container.file },
            exposureConfiguration.v1Config,
            token
        )
    }

    private data class DiagnosisKeysContainer(
        val diagnosisKeysFileModel: DiagnosisKeysFileModel,
        val file: File,
    )
}
