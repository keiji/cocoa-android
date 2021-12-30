package dev.keiji.cocoa.android

import android.app.Activity
import android.content.Context
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
import dev.keiji.cocoa.android.exposure_notification.cappuccino.toEnTimeWindow
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.ExposureDetectionService
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.source.DateTimeSource
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import kotlin.random.Random

class ExposureNotificationWrapperMock(
    private val applicationContext: Context,
    private val dateTimeSource: DateTimeSource,
    pathSource: PathSource,
    private val exposureDetectionService: ExposureDetectionService,
) : ExposureNotificationWrapper {

    companion object {
        private const val MAX_TEK_COUNT = 14
    }

    private val dummyExposureInfos =
        File(pathSource.dummyExposureDataDir(), PathSource.FILENAME_DUMMY_EXPOSURE_INFOS_DATA)
    private val dummuExposureSummary =
        File(pathSource.dummyExposureDataDir(), PathSource.FILENAME_DUMMY_EXPOSURE_SUMMARY_DATA)
    private val dummyDailySummaries =
        File(pathSource.dummyExposureDataDir(), PathSource.FILENAME_DUMMY_DAILY_SUMMARY_DATA)
    private val dummyExposureWindows =
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
            val date = dateTimeSource.today().also {
                it.add(Calendar.DAY_OF_MONTH, -offsetDays)
            }
            TemporaryExposureKey.createDummy(random, date.time.toEnTimeWindow())
        }
    }

    override suspend fun getExposureWindow(): List<ExposureWindow> {
        if (!dummyExposureWindows.exists()) {
            return emptyList()
        }

        val jsonText = dummyExposureWindows.readText()
        return Json.decodeFromString(jsonText)
    }

    override suspend fun getDailySummary(dailySummariesConfig: ExposureConfiguration.DailySummariesConfig): List<DailySummary> {
        if (!dummyDailySummaries.exists()) {
            return emptyList()
        }

        val jsonText = dummyDailySummaries.readText()
        return Json.decodeFromString(jsonText)
    }

    override suspend fun getExposureSummary(token: String): ExposureSummary {
        if (!dummuExposureSummary.exists()) {
            return ExposureSummary(IntArray(0), 0, 0, 0, 0)
        }

        val jsonText = dummuExposureSummary.readText()
        return Json.decodeFromString(jsonText)
    }

    override suspend fun getExposureInformation(token: String): List<ExposureInformation> {
        if (!dummyExposureInfos.exists()) {
            return emptyList()
        }

        val jsonText = dummyExposureInfos.readText()
        return Json.decodeFromString(jsonText)
    }

    override suspend fun provideDiagnosisKeys(diagnosisKeyFileList: List<File>) {
        if (dummyDailySummaries.exists()) {
            val enVersion = getVersion()
            val dailySummaryList = getDailySummary(ExposureConfiguration.DailySummariesConfig())
            val exposureWindowList = getExposureWindow()

            exposureDetectionService.v2ExposureDetectedWork(
                enVersion,
                dailySummaryList,
                exposureWindowList,
                ExposureConfiguration()
            )
        }
    }

    override suspend fun provideDiagnosisKeys(diagnosisKeyFileProvider: DiagnosisKeyFileProvider) {
        if (dummyDailySummaries.exists()) {
            val enVersion = getVersion()
            val dailySummaryList = getDailySummary(ExposureConfiguration.DailySummariesConfig())
            val exposureWindowList = getExposureWindow()

            exposureDetectionService.v2ExposureDetectedWork(
                enVersion,
                dailySummaryList,
                exposureWindowList,
                ExposureConfiguration(),
            )
        }
    }

    override suspend fun provideDiagnosisKeys(
        diagnosisKeyFileList: List<File>,
        exposureConfiguration: ExposureConfiguration.V1Config,
        token: String
    ) {
        if (dummuExposureSummary.exists()) {
            val enVersion = getVersion()
            val exposureSummary = getExposureSummary(token)
            val exposureInformationList = getExposureInformation(token)

            exposureDetectionService.v1ExposureDetectedWork(
                enVersion,
                exposureSummary,
                exposureInformationList,
                ExposureConfiguration()
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