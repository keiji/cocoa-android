package dev.keiji.cocoa.android

import android.app.Activity
import android.os.Bundle
import dev.keiji.cocoa.android.exposure_notification.cappuccino.DiagnosisKeyFileProvider
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.DailySummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureConfiguration
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureInformation
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureNotificationStatus
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureSummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureWindow
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.PackageConfiguration
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.TemporaryExposureKey
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.cappuccino.toEnTimeWindow
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.ExposureResultService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.random.Random

class ExposureNotificationWrapperMock(
    private val dateTimeSource: DateTimeSource,
    pathSource: PathSource,
    private val exposuredaService: ExposureResultService
) : ExposureNotificationWrapper {

    companion object {
        private const val MAX_TEK_COUNT = 14
    }

    private val dummyExposureInfosFile =
        File(pathSource.dummyExposureDataDir(), PathSource.FILENAME_DUMMY_EXPOSURE_INFOS_DATA)
    private val dummyExposureSummaryFile =
        File(pathSource.dummyExposureDataDir(), PathSource.FILENAME_DUMMY_EXPOSURE_SUMMARY_DATA)
    private val dummyDailySummariesFile =
        File(pathSource.dummyExposureDataDir(), PathSource.FILENAME_DUMMY_DAILY_SUMMARY_DATA)
    private val dummyExposureWindowsFile =
        File(pathSource.dummyExposureDataDir(), PathSource.FILENAME_DUMMY_EXPOSURE_WINDOW_DATA)

    private var isStarted: Boolean = false

    override suspend fun start(activity: Activity) {
        isStarted = true
    }

    override suspend fun stop(activity: Activity) {
        isStarted = false
    }

    override suspend fun getVersion(): Long = -1

    override suspend fun isEnabled(): Boolean = isStarted

    override suspend fun getStatuses(): List<ExposureNotificationStatus> {
        return listOf(ExposureNotificationStatus.ACTIVATED)
    }

    override suspend fun getCalibrationConfidence(): Int {
        return 0
    }

    private var diagnosisKeysDataMappingConfig: ExposureConfiguration.DiagnosisKeysDataMappingConfig =
        ExposureConfiguration.DiagnosisKeysDataMappingConfig(
            infectiousnessWhenDaysSinceOnsetMissing = 1,
            reportTypeWhenMissing = 2,
        )

    override suspend fun getDiagnosisKeysDataMapping(): ExposureConfiguration.DiagnosisKeysDataMappingConfig {
        return diagnosisKeysDataMappingConfig
    }

    override suspend fun setDiagnosisKeysDataMapping(diagnosisKeysDataMapping: ExposureConfiguration.DiagnosisKeysDataMappingConfig) {
        diagnosisKeysDataMappingConfig = diagnosisKeysDataMapping
    }

    override suspend fun getPackageConfiguration(): PackageConfiguration {
        return PackageConfiguration(Bundle())
    }

    override suspend fun getTemporaryExposureKeyHistory(activity: Activity): List<TemporaryExposureKey> {
        val random = Random(System.currentTimeMillis())
        val keyCount = random.nextInt(MAX_TEK_COUNT)
        return (0..keyCount).map { num ->
            val offsetDays = num + 1
            val dateTime = dateTimeSource.offsetDateTime(-offsetDays)
            TemporaryExposureKey.createDummy(random, dateTime.toEnTimeWindow())
        }
    }

    override suspend fun getExposureWindow(): List<ExposureWindow> = withContext(Dispatchers.IO) {
        if (!dummyExposureWindowsFile.exists()) {
            return@withContext emptyList()
        }

        val jsonText = dummyExposureWindowsFile.readText()
        return@withContext Json.decodeFromString(jsonText)
    }

    override suspend fun getDailySummary(dailySummariesConfig: ExposureConfiguration.DailySummariesConfig): List<DailySummary> =
        withContext(Dispatchers.IO) {
            if (!dummyDailySummariesFile.exists()) {
                return@withContext emptyList()
            }

            val jsonText = dummyDailySummariesFile.readText()
            return@withContext Json.decodeFromString(jsonText)
        }

    override suspend fun getExposureSummary(token: String): ExposureSummary =
        withContext(Dispatchers.IO) {
            if (!dummyExposureSummaryFile.exists()) {
                return@withContext ExposureSummary(IntArray(0), 0, 0, 0, 0)
            }

            val jsonText = dummyExposureSummaryFile.readText()
            return@withContext Json.decodeFromString(jsonText)
        }

    override suspend fun getExposureInformation(token: String): List<ExposureInformation> =
        withContext(Dispatchers.IO) {
            if (!dummyExposureInfosFile.exists()) {
                return@withContext emptyList()
            }

            val jsonText = dummyExposureInfosFile.readText()
            return@withContext Json.decodeFromString(jsonText)
        }

    override suspend fun provideDiagnosisKeys(diagnosisKeyFileList: List<File>) {
        if (dummyDailySummariesFile.exists()) {
            exposuredaService.onExposureDetected(
                dailySummaryList = getDailySummary(ExposureConfiguration.DailySummariesConfig()),
                exposureWindowList = getExposureWindow(),
            )
        }
    }

    override suspend fun provideDiagnosisKeys(diagnosisKeyFileProvider: DiagnosisKeyFileProvider) {
        if (dummyDailySummariesFile.exists()) {
            exposuredaService.onExposureDetected(
                dailySummaryList = getDailySummary(ExposureConfiguration.DailySummariesConfig()),
                exposureWindowList = getExposureWindow(),
            )
        }
    }

    override suspend fun provideDiagnosisKeys(
        diagnosisKeyFileList: List<File>,
        exposureConfiguration: ExposureConfiguration.V1Config,
        token: String
    ) {
        if (dummyExposureSummaryFile.exists()) {
            exposuredaService.onExposureDetected(
                exposureSummary = getExposureSummary(""),
                exposureInformationList = getExposureInformation(""),
            )
        }
    }

    override suspend fun requestPreAuthorizedTemporaryExposureKeyHistory(activity: Activity) {
        TODO("Not yet implemented")
    }

    override suspend fun requestPreAuthorizedTemporaryExposureKeyRelease() {
        TODO("Not yet implemented")
    }

    override suspend fun requestPreAuthorizedTemporaryExposureKeyHistoryForSelfReport() {
        TODO("Not yet implemented")
    }
}
