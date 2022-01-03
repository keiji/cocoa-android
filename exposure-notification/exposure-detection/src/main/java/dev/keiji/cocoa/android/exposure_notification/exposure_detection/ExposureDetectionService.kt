package dev.keiji.cocoa.android.exposure_notification.exposure_detection

import android.content.Intent
import android.os.Build
import android.os.SystemClock
import androidx.work.ListenableWorker
import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureNotificationStatus
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataCollectionApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataRequest
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.DiagnosisKeysFileRepository
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepository
import dev.keiji.cocoa.android.exposure_notification.model.DiagnosisKeysFileModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataBaseModel
import dev.keiji.cocoa.android.exposure_notification.repository.ExposureDataRepository
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import timber.log.Timber
import java.io.File
import java.util.*

interface ExposureDetectionService {
    fun isExposureNotificationEnabled(intent: Intent): Boolean

    suspend fun detectExposure(): ListenableWorker.Result

    suspend fun onResultReceived(intentAction: String)

    suspend fun noExposureDetectedWork(): ListenableWorker.Result

    suspend fun v1ExposureDetectedWork(
        token: String,
    ): ListenableWorker.Result

    suspend fun v2ExposureDetectedWork(): ListenableWorker.Result
}

class ExposureDetectionServiceImpl(
    private val dateTimeSource: DateTimeSource,
    private val exposureConfigurationRepository: ExposureConfigurationRepository,
    private val exposureDataRepository: ExposureDataRepository,
    private val exposureDataCollectionApi: ExposureDataCollectionApi,
    private val diagnosisKeysFileRepository: DiagnosisKeysFileRepository,
    private val configurationSource: ConfigurationSource,
    private val exposureResultService: ExposureResultService,
    private val exposureNotificationWrapper: ExposureNotificationWrapper,
) : ExposureDetectionService {
    companion object {
        private const val TIMEOUT_INTERVAL_IN_MILLIS = 1000 * 60 * 60 * 2
    }

    override fun isExposureNotificationEnabled(intent: Intent): Boolean =
        intent.getBooleanExtra(ExposureNotificationWrapper.EXTRA_SERVICE_STATE, false)

    override suspend fun detectExposure(): ListenableWorker.Result {
        if (!exposureNotificationWrapper.isEnabled()) {
            Timber.w("ExposureNotification is disabled.")
            return ListenableWorker.Result.failure()
        }

        if (!exposureNotificationWrapper.getStatuses()
                .any { it == ExposureNotificationStatus.ACTIVATED }
        ) {
            Timber.w("ExposureNotification is not activated.")
            return ListenableWorker.Result.failure()
        }

        // Sub-region
        val subregions = mutableListOf<String>()
        val subRegionDiagnosisKeyFiles = mutableListOf<DiagnosisKeysContainer>()

        configurationSource.regions.forEach { region ->
            configurationSource.subregions.forEach { subregion ->
                val list = downloadDiagnosisKeys(region.toString(), subregion)
                if (list.isNotEmpty()) {
                    subregions.add(subregion)
                    subRegionDiagnosisKeyFiles.addAll(list)
                }
            }
            if (subRegionDiagnosisKeyFiles.isNotEmpty()) {
                Timber.i("subregion DiagnosisKeys found. ${subRegionDiagnosisKeyFiles.size}")
                detectExposure(
                    region = region.toString(),
                    subregionList = subregions,
                    diagnosisKeysFileContainerList = subRegionDiagnosisKeyFiles,
                )
            }

            // Region
            val diagnosisKeyFiles = downloadDiagnosisKeys(
                region.toString(),
                null
            )
            if (diagnosisKeyFiles.isNotEmpty()) {
                Timber.i("region DiagnosisKeys found. ${subRegionDiagnosisKeyFiles.size}")
                detectExposure(
                    region = region.toString(),
                    diagnosisKeysFileContainerList = diagnosisKeyFiles,
                )
            }
        }
        return ListenableWorker.Result.success()
    }

    private suspend fun downloadDiagnosisKeys(
        region: String,
        subregion: String?
    ): List<DiagnosisKeysContainer> {
        val diagnosisKeyList =
            diagnosisKeysFileRepository.getDiagnosisKeysFileList(region, subregion)

        Timber.d("Region:$region, Subregion:${subregion}, diagnosisKeyList: ${diagnosisKeyList.size}")

        val diagnosisKeysContainers: MutableList<DiagnosisKeysContainer> = mutableListOf()

        diagnosisKeyList.forEach { diagnosisKeyEntry ->
            Timber.d(diagnosisKeyEntry.toString())
            val downloadedFile =
                diagnosisKeysFileRepository.getDiagnosisKeysFile(diagnosisKeyEntry)
                    ?: return@forEach
            diagnosisKeyEntry.also { model ->
                model.state = DiagnosisKeysFileModel.State.Downloaded.value
                model.filePath = downloadedFile.absolutePath
                diagnosisKeysFileRepository.upsert(model)
            }

            diagnosisKeysContainers.add(
                DiagnosisKeysContainer(
                    diagnosisKeyEntry,
                    downloadedFile
                )
            )
        }

        return diagnosisKeysContainers
    }

    suspend fun onStarted(
        exposureNotificationWrapper: ExposureNotificationWrapper,
        region: String,
        subregionList: List<String>,
        diagnosisKeysFileContainerList: List<DiagnosisKeysContainer>,
    ) {
        Timber.d("started: onStarted ${dateTimeSource.epochInMillis()}")

        val baseTimeInMillis = dateTimeSource.epochInMillis() - TIMEOUT_INTERVAL_IN_MILLIS

        exposureDataRepository.setTimeout(
            baseTimeInMillis,
            ExposureDataBaseModel.State.Started
        )
        exposureDataRepository.setTimeout(
            baseTimeInMillis,
            ExposureDataBaseModel.State.ResultReceived
        )

        val exposureDataBaseModel = ExposureDataBaseModel(
            id = 0,
            region = region,
            subregionList = subregionList,
            enVersion = exposureNotificationWrapper.getVersion().toString(),
            stateValue = ExposureDataBaseModel.State.Started.value,
            startedEpoch = dateTimeSource.epochInMillis(),
            startUptime = SystemClock.uptimeMillis(),
        )

        val diagnosisKeysFileList = diagnosisKeysFileContainerList.map { container ->
            container.diagnosisKeysFileModel.state = DiagnosisKeysFileModel.State.Completed.value
            container.diagnosisKeysFileModel.filePath = null
            return@map container.diagnosisKeysFileModel
        }
        diagnosisKeysFileRepository.upsertDiagnosisKeysFile(diagnosisKeysFileList)

        exposureDataRepository.upsert(
            exposureDataBaseModel,
            diagnosisKeysFileList = diagnosisKeysFileList,
        )

        Timber.d("finished: onStarted ${dateTimeSource.epochInMillis()}")
    }

    override suspend fun onResultReceived(
        intentAction: String
    ) {
        Timber.d("started: onResultReceived ${dateTimeSource.epochInMillis()}")

        val epochInMillis = dateTimeSource.epochInMillis()
        exposureDataRepository.setTimeout(
            epochInMillis - TIMEOUT_INTERVAL_IN_MILLIS,
            ExposureDataBaseModel.State.Started
        )

        val exposureDataStartedList =
            exposureDataRepository.findBy(ExposureDataBaseModel.State.Started)

        val exposureDataStarted = if (exposureDataStartedList.isEmpty()) {
            Timber.e("exposureDataStarted object not found.")
            return
        } else if (exposureDataStartedList.size > 1) {
            Timber.w("exposureDataStarted found multiple.")
            exposureDataStartedList.first()
        } else {
            exposureDataStartedList.first()
        }

        exposureDataStarted.exposureBaseData.state = ExposureDataBaseModel.State.ResultReceived
        exposureDataRepository.upsert(exposureDataStarted)

        Timber.d("finished: onResultReceived ${dateTimeSource.epochInMillis()}")
    }

    override suspend fun noExposureDetectedWork(): ListenableWorker.Result {
        val enVersion = exposureNotificationWrapper.getVersion()
        val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()

        try {
            configurationSource.regions.forEach { region ->
                exposureDataCollectionApi.submit(
                    region.toString(),
                    ExposureDataRequest(
                        device = Build.MODEL,
                        enVersion = enVersion.toString(),
                        exposureConfiguration = exposureConfiguration,
                        null,
                        null,
                        null,
                        null
                    )
                )
            }
            exposureResultService.onNoExposureDetected()
        } catch (exception: Exception) {
            Timber.e(exception)
        }
        return ListenableWorker.Result.success()
    }

    override suspend fun v1ExposureDetectedWork(
        token: String,
    ): ListenableWorker.Result {
        val enVersion = exposureNotificationWrapper.getVersion()
        val exposureSummary = exposureNotificationWrapper.getExposureSummary(token)
        val exposureInformationList = exposureNotificationWrapper.getExposureInformation(token)
        val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()

        try {
            configurationSource.regions.forEach { region ->
                exposureDataCollectionApi.submit(
                    region.toString(),
                    ExposureDataRequest(
                        device = Build.MODEL,
                        enVersion = enVersion.toString(),
                        exposureConfiguration = exposureConfiguration,
                        exposureSummary = exposureSummary,
                        exposureInformationList = exposureInformationList,
                        dailySummaryList = null,
                        exposureWindowList = null
                    )
                )
            }

            exposureResultService.onExposureDetected(
                exposureInformationList = exposureInformationList,
            )
        } catch (exception: Exception) {
            Timber.e(exception)
        }

        return ListenableWorker.Result.success()
    }

    override suspend fun v2ExposureDetectedWork(): ListenableWorker.Result {
        val enVersion = exposureNotificationWrapper.getVersion()
        val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()

        val dailySummaryList =
            exposureNotificationWrapper.getDailySummary(exposureConfiguration.dailySummaryConfig)
        val exposureWindowList = exposureNotificationWrapper.getExposureWindow()

        try {
            configurationSource.regions.forEach { region ->
                exposureDataCollectionApi.submit(
                    region.toString(),
                    ExposureDataRequest(
                        device = Build.MODEL,
                        enVersion = enVersion.toString(),
                        exposureConfiguration = exposureConfiguration,
                        exposureSummary = null,
                        exposureInformationList = null,
                        dailySummaryList = dailySummaryList,
                        exposureWindowList = exposureWindowList
                    )
                )
            }
        } catch (exception: Exception) {
            Timber.e(exception)
        }

        return ListenableWorker.Result.success()
    }

    private suspend fun detectExposure(
        region: String,
        subregionList: List<String> = emptyList(),
        diagnosisKeysFileContainerList: List<DiagnosisKeysContainer>
    ) {
        if (configurationSource.isEnabledExposureWindowMode()) {
            detectExposureExposureWindowMode(diagnosisKeysFileContainerList)
        } else {
            detectExposureLegacyV1(diagnosisKeysFileContainerList)
        }

        onStarted(
            exposureNotificationWrapper,
            region,
            subregionList,
            diagnosisKeysFileContainerList,
        )

        diagnosisKeysFileContainerList.forEach { container ->
            container.file.delete()
        }
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

    data class DiagnosisKeysContainer(
        val diagnosisKeysFileModel: DiagnosisKeysFileModel,
        val file: File,
    )
}
