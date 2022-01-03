package dev.keiji.cocoa.android.exposure_notification.exposure_detection

import android.content.Intent
import android.os.Build
import android.os.SystemClock
import androidx.work.ListenableWorker
import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationException
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureNotificationStatus
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataCollectionApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataRequest
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.DiagnosisKeysFileRepository
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepository
import dev.keiji.cocoa.android.exposure_notification.model.DiagnosisKeysFileModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataBaseModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataModel
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

    private suspend fun reloadFileList(): List<DiagnosisKeysFileModel> {
        val diagnosisKeyList = mutableListOf<DiagnosisKeysFileModel>()
        configurationSource.regions.forEach { region ->
            configurationSource.subregions.forEach { subregion ->
                val list = diagnosisKeysFileRepository.getDiagnosisKeysFileList(
                    region.toString(),
                    subregion
                )
                diagnosisKeyList.addAll(list)
            }

            val list = diagnosisKeysFileRepository.getDiagnosisKeysFileList(
                region.toString(),
                null,
            )
            diagnosisKeyList.addAll(list)
        }
        return diagnosisKeyList
    }

    private suspend fun planExposureDetection(
        enVersion: String,
        diagnosisKeysFileList: List<DiagnosisKeysFileModel>
    ) {
        // Timeout
        exposureDataRepository.setTimeout(
            dateTimeSource.epochInMillis() - TIMEOUT_INTERVAL_IN_MILLIS,
            ExposureDataBaseModel.State.Planned
        )

        val newDiagnosisKeysFileList = diagnosisKeysFileList
            .filter { diagnosisKeysFile -> diagnosisKeysFile.exposureDataId == 0L }

        val regions = newDiagnosisKeysFileList
            .filter { diagnosisKeysFile -> diagnosisKeysFile.subregion == null }
            .map { diagnosisKeysFile -> diagnosisKeysFile.region }
            .distinct()

        regions.forEach { region ->

            // region
            val regionDiagnosisKeysFileList = newDiagnosisKeysFileList
                .filter { diagnosisKeysFile -> diagnosisKeysFile.region == region }
                .filter { diagnosisKeysFile -> diagnosisKeysFile.subregion == null }

            val exposureData = ExposureDataBaseModel(
                id = 0,
                region = region,
                subregionList = emptyList(),
                enVersion = enVersion,
                stateValue = ExposureDataBaseModel.State.Planned.value,
                plannedEpoch = dateTimeSource.epochInMillis(),
            )
            exposureDataRepository.upsert(exposureData, regionDiagnosisKeysFileList)

            // subregion
            val subregions = newDiagnosisKeysFileList
                .filter { diagnosisKeysFile -> diagnosisKeysFile.region == region }
                .filter { diagnosisKeysFile -> diagnosisKeysFile.subregion != null }
                .mapNotNull { diagnosisKeysFile -> diagnosisKeysFile.subregion }
                .distinct()

            subregions.forEach { subregion ->
                val subregionDiagnosisKeysFileList = newDiagnosisKeysFileList
                    .filter { diagnosisKeysFile -> diagnosisKeysFile.region == region }
                    .filter { diagnosisKeysFile -> diagnosisKeysFile.subregion == subregion }

                val exposureData = ExposureDataBaseModel(
                    id = 0,
                    region = region,
                    subregionList = listOf(subregion),
                    enVersion = enVersion,
                    stateValue = ExposureDataBaseModel.State.Planned.value,
                    plannedEpoch = dateTimeSource.epochInMillis(),
                )
                exposureDataRepository.upsert(exposureData, subregionDiagnosisKeysFileList)
            }
        }
    }

    override suspend fun detectExposure(): ListenableWorker.Result {
        Timber.d("start: detectExposure().")

        exposureDataRepository.cleanupTimeout()

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

        val enVersion = exposureNotificationWrapper.getVersion().toString()
        Timber.d("EN Version: $enVersion")

        val diagnosisKeysFileList = reloadFileList()
        planExposureDetection(enVersion, diagnosisKeysFileList)

        exposureDataRepository.setTimeout(
            dateTimeSource.epochInMillis() - TIMEOUT_INTERVAL_IN_MILLIS,
            ExposureDataBaseModel.State.Started
        )

        val taskStarted = exposureDataRepository.getBy(ExposureDataBaseModel.State.Started)
        val taskReceived = exposureDataRepository.getBy(ExposureDataBaseModel.State.ResultReceived)
        if (taskStarted != null || taskReceived != null) {
            Timber.w("Task already in progress.")
            return ListenableWorker.Result.retry()
        }

        var taskPlanned = exposureDataRepository
            .findBy(ExposureDataBaseModel.State.Planned)
            .firstOrNull { taskPlanned -> taskPlanned.exposureBaseData.subregionList.isNotEmpty() }
        if (taskPlanned == null) {
            taskPlanned = exposureDataRepository.getBy(ExposureDataBaseModel.State.Planned)
        }
        taskPlanned ?: return ListenableWorker.Result.success()

        startExposure(taskPlanned)

        Timber.w("Task started. $taskPlanned")

        Timber.d("finish: detectExposure().")
        return ListenableWorker.Result.success()
    }

    private suspend fun startExposure(exposureDataModel: ExposureDataModel) {
        Timber.d("start: startExposure")

        val diagnosisKeysFileContainerList =
            downloadDiagnosisKeys(exposureDataModel.diagnosisKeysFileList)

        try {
            if (configurationSource.isEnabledExposureWindowMode()) {
                detectExposureExposureWindowMode(diagnosisKeysFileContainerList)
            } else {
                detectExposureLegacyV1(diagnosisKeysFileContainerList)
            }

            onStarted(exposureDataModel)

            diagnosisKeysFileContainerList.forEach { container ->
                container.file.delete()
            }
        } catch (exception: ExposureNotificationException) {
            Timber.d(exposureDataModel.toString())

            exposureDataModel.exposureBaseData.also { exposureBaseData ->
                exposureBaseData.priority -= 10
                exposureBaseData.message = exception.message
            }
            exposureDataRepository.upsert(exposureDataModel)

            Timber.d(exposureDataModel.toString())
        }

        Timber.d("finish: startExposure")
    }

    private suspend fun downloadDiagnosisKeys(
        diagnosisKeyList: List<DiagnosisKeysFileModel>
    ): List<DiagnosisKeysContainer> {
        val diagnosisKeysContainerList: MutableList<DiagnosisKeysContainer> = mutableListOf()

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

            diagnosisKeysContainerList.add(
                DiagnosisKeysContainer(
                    diagnosisKeyEntry,
                    downloadedFile
                )
            )
        }

        return diagnosisKeysContainerList
    }

    private suspend fun onStarted(exposureDataModel: ExposureDataModel) {
        Timber.d("started: onStarted ${dateTimeSource.epochInMillis()}")

        exposureDataRepository.setTimeout(
            dateTimeSource.epochInMillis() - TIMEOUT_INTERVAL_IN_MILLIS,
            ExposureDataBaseModel.State.Started
        )

        exposureDataModel.exposureBaseData.also { exposureBaseData ->
            exposureBaseData.startUptime = SystemClock.uptimeMillis()
            exposureBaseData.startedEpoch = dateTimeSource.epochInMillis()
            exposureBaseData.state = ExposureDataBaseModel.State.Started
        }
        exposureDataModel.diagnosisKeysFileList.forEach { diagnosisKeysFileModel ->
            diagnosisKeysFileModel.state = DiagnosisKeysFileModel.State.Completed.value
            diagnosisKeysFileModel.filePath = null
        }
        exposureDataRepository.upsert(exposureDataModel)

        Timber.d("finished: onStarted ${dateTimeSource.epochInMillis()}")
    }

    override suspend fun onResultReceived(intentAction: String) {
        Timber.d("started: onResultReceived ${dateTimeSource.epochInMillis()}")

        val epochInMillis = dateTimeSource.epochInMillis()
        exposureDataRepository.setTimeout(
            epochInMillis - TIMEOUT_INTERVAL_IN_MILLIS,
            ExposureDataBaseModel.State.ResultReceived
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

        exposureDataStarted.exposureBaseData.also { exposureBaseData ->
            exposureBaseData.state = ExposureDataBaseModel.State.ResultReceived
        }
        exposureDataRepository.upsert(exposureDataStarted)

        Timber.d("finished: onResultReceived ${dateTimeSource.epochInMillis()}")
    }

    override suspend fun noExposureDetectedWork(): ListenableWorker.Result {
        Timber.d("started: noExposureDetectedWork ${dateTimeSource.epochInMillis()}")

        val taskResultReceived =
            exposureDataRepository.getBy(ExposureDataBaseModel.State.ResultReceived)
        if (taskResultReceived == null) {
            Timber.w("taskResultReceived not found.")
            return ListenableWorker.Result.success()
        }

        val enVersion = exposureNotificationWrapper.getVersion()
        val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()

        try {
            exposureResultService.onExposureNotDetected()

            exposureDataCollectionApi.submit(
                taskResultReceived.exposureBaseData.region,
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
        } catch (exception: Exception) {
            Timber.e(exception)
        }

        Timber.d("finished: noExposureDetectedWork ${dateTimeSource.epochInMillis()}")

        taskResultReceived.exposureBaseData.also { exposureDataBaseModel ->
            exposureDataBaseModel.finishedEpoch = dateTimeSource.epochInMillis()
            exposureDataBaseModel.state = ExposureDataBaseModel.State.Finished
        }
        exposureDataRepository.upsert(taskResultReceived)

        return detectExposure()
    }

    override suspend fun v1ExposureDetectedWork(
        token: String,
    ): ListenableWorker.Result {
        Timber.d("started: v1ExposureDetectedWork ${dateTimeSource.epochInMillis()}")

        val taskResultReceived =
            exposureDataRepository.getBy(ExposureDataBaseModel.State.ResultReceived)
        if (taskResultReceived == null) {
            Timber.w("taskResultReceived not found.")
            return ListenableWorker.Result.success()
        }

        val exposureSummary = exposureNotificationWrapper.getExposureSummary(token)
        val exposureInformationList = exposureNotificationWrapper.getExposureInformation(token)
        val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()

        try {
            exposureResultService.onExposureDetected(
                exposureSummary = exposureSummary,
                exposureInformationList = exposureInformationList,
            )

            exposureDataCollectionApi.submit(
                taskResultReceived.exposureBaseData.region,
                ExposureDataRequest(
                    device = Build.MODEL,
                    enVersion = taskResultReceived.exposureBaseData.enVersion,
                    exposureConfiguration = exposureConfiguration,
                    exposureSummary = exposureSummary,
                    exposureInformationList = exposureInformationList,
                    dailySummaryList = null,
                    exposureWindowList = null
                )
            )
        } catch (exception: Exception) {
            Timber.e(exception)
        }

        Timber.d("finished: v1ExposureDetectedWork ${dateTimeSource.epochInMillis()}")

        taskResultReceived.exposureBaseData.also { exposureDataBaseModel ->
            exposureDataBaseModel.finishedEpoch = dateTimeSource.epochInMillis()
            exposureDataBaseModel.state = ExposureDataBaseModel.State.Finished
        }
        exposureDataRepository.upsert(taskResultReceived)

        return detectExposure()
    }

    override suspend fun v2ExposureDetectedWork(): ListenableWorker.Result {
        Timber.d("started: v2ExposureDetectedWork ${dateTimeSource.epochInMillis()}")

        val taskResultReceived =
            exposureDataRepository.getBy(ExposureDataBaseModel.State.ResultReceived)
        if (taskResultReceived == null) {
            Timber.w("taskResultReceived not found.")
            return ListenableWorker.Result.success()
        }

        val exposureConfiguration = exposureConfigurationRepository.getExposureConfiguration()
        val dailySummaryList =
            exposureNotificationWrapper.getDailySummary(exposureConfiguration.dailySummaryConfig)
        val exposureWindowList = exposureNotificationWrapper.getExposureWindow()

        try {
            exposureResultService.onExposureDetected(
                dailySummaryList = dailySummaryList,
                exposureWindowList = exposureWindowList,
            )

            exposureDataCollectionApi.submit(
                taskResultReceived.exposureBaseData.region,
                ExposureDataRequest(
                    device = Build.MODEL,
                    enVersion = taskResultReceived.exposureBaseData.enVersion,
                    exposureConfiguration = exposureConfiguration,
                    exposureSummary = null,
                    exposureInformationList = null,
                    dailySummaryList = dailySummaryList,
                    exposureWindowList = exposureWindowList
                )
            )

        } catch (exception: Exception) {
            Timber.e(exception)
        }

        Timber.d("finished: v2ExposureDetectedWork ${dateTimeSource.epochInMillis()}")

        taskResultReceived.exposureBaseData.also { exposureDataBaseModel ->
            exposureDataBaseModel.finishedEpoch = dateTimeSource.epochInMillis()
            exposureDataBaseModel.state = ExposureDataBaseModel.State.Finished
        }
        exposureDataRepository.upsert(taskResultReceived)

        return detectExposure()
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
