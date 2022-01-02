package dev.keiji.cocoa.android.exposure_notification.exposure_detection

import android.content.Intent
import android.os.Build
import androidx.work.ListenableWorker
import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.DailySummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureInformation
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureNotificationStatus
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureSummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureWindow
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

    suspend fun onStarted(
        exposureNotificationWrapper: ExposureNotificationWrapper,
        region: String,
        subregionList: List<String>,
        diagnosisKeysFileList: List<DiagnosisKeysFileModel>,
    )

    suspend fun onResultReceived(intentAction: String)

    suspend fun onFinished(
        exposureSummary: ExposureSummary? = null,
        exposureInformationList: List<ExposureInformation> = listOf(),
        dailySummaryList: List<DailySummary> = listOf(),
        exposureWindowList: List<ExposureWindow> = listOf(),
    )

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

        val exposureWindowList = exposureNotificationWrapper.getExposureWindow()
        if (exposureWindowList.isNotEmpty()) {
            Timber.w("Current ExposureWindow detected. ${exposureWindowList.size}")
            Timber.w(exposureWindowList.toString())
        } else {
            Timber.w("ExposureWindow not detected.")
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

    override suspend fun onStarted(
        exposureNotificationWrapper: ExposureNotificationWrapper,
        region: String,
        subregionList: List<String>,
        diagnosisKeysFileList: List<DiagnosisKeysFileModel>,
    ) {
        Timber.d("started: onStarted ${dateTimeSource.epoch()}")

        val baseTimeInMillis = (dateTimeSource.epoch() * 1000) - TIMEOUT_INTERVAL_IN_MILLIS

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
            startEpoch = dateTimeSource.epoch(),
        )
        exposureDataRepository.upsert(
            exposureDataBaseModel,
            diagnosisKeysFileList = diagnosisKeysFileList,
        )

        Timber.d("finished: onStarted ${dateTimeSource.epoch()}")
    }

    override suspend fun onResultReceived(
        intentAction: String
    ) {
        Timber.d("started: onResultReceived ${dateTimeSource.epoch()}")

        val epochInMillis = dateTimeSource.epoch() * 1000
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

        Timber.d("finished: onResultReceived ${dateTimeSource.epoch()}")
    }

    override suspend fun onFinished(
        exposureSummary: ExposureSummary?,
        exposureInformationList: List<ExposureInformation>,
        dailySummaryList: List<DailySummary>,
        exposureWindowList: List<ExposureWindow>,
    ) {
        Timber.d("started: onFinished ${dateTimeSource.epoch()}")

        val epochInMillis = dateTimeSource.epoch() * 1000
        exposureDataRepository.setTimeout(
            epochInMillis - TIMEOUT_INTERVAL_IN_MILLIS,
            ExposureDataBaseModel.State.Started
        )

        val exposureDataResultReceivedList =
            exposureDataRepository.findBy(ExposureDataBaseModel.State.ResultReceived)

        val exposureDataResultReceived = if (exposureDataResultReceivedList.isEmpty()) {
            Timber.e("exposureDataResultReceived object not found.")
            return
        } else if (exposureDataResultReceivedList.size > 1) {
            Timber.w("exposureDataResultReceived found multiple.")
            exposureDataResultReceivedList.first()
        } else {
            exposureDataResultReceivedList.first()
        }

        exposureDataRepository.upsert(
            exposureBaseData = exposureDataResultReceived.exposureBaseData,
            diagnosisKeysFileList = exposureDataResultReceived.diagnosisKeysFileList,
            exposureSummary = exposureSummary,
            exposureInformationList = exposureInformationList,
            dailySummaryList = dailySummaryList,
            exposureWindowList = exposureWindowList
        )

        Timber.d("finished: onFinished ${dateTimeSource.epoch()}")
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

            onFinished(
                // No data
            )
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

            onFinished(
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

        val diagnosisKeysFileList = diagnosisKeysFileContainerList.map { container ->
            container.diagnosisKeysFileModel.state = DiagnosisKeysFileModel.State.Completed.value
            container.diagnosisKeysFileModel.filePath = null
            return@map container.diagnosisKeysFileModel
        }
        diagnosisKeysFileRepository.upsertDiagnosisKeysFile(diagnosisKeysFileList)

        onStarted(
            exposureNotificationWrapper,
            region,
            subregionList,
            diagnosisKeysFileList,
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

    private data class DiagnosisKeysContainer(
        val diagnosisKeysFileModel: DiagnosisKeysFileModel,
        val file: File,
    )
}
