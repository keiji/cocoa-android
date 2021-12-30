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
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.ExposureDetectionService
import java.io.File

class ExposureNotificationWrapperMock(
    private val applicationContext: Context,
    private var exposureDetectionService: ExposureDetectionService,
    ) : ExposureNotificationWrapper {

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

    override suspend fun getExposureWindow(): List<ExposureWindow> {
        TODO("Not yet implemented")
    }

    override suspend fun getDailySummary(dailySummariesConfig: ExposureConfiguration.DailySummariesConfig): List<DailySummary> {
        TODO("Not yet implemented")
    }

    override suspend fun getExposureSummary(token: String): ExposureSummary {
        TODO("Not yet implemented")
    }

    override suspend fun getExposureInformation(token: String): List<ExposureInformation> {
        TODO("Not yet implemented")
    }

    override suspend fun getTemporaryExposureKeyHistory(activity: Activity): List<TemporaryExposureKey>? {
        TODO("Not yet implemented")
    }

    override suspend fun provideDiagnosisKeys(diagnosisKeyFileList: List<File>) {
        TODO("Not yet implemented")
    }

    override suspend fun provideDiagnosisKeys(diagnosisKeyFileProvider: DiagnosisKeyFileProvider) {
        TODO("Not yet implemented")
    }

    override suspend fun provideDiagnosisKeys(
        diagnosisKeyFileList: List<File>,
        exposureConfiguration: ExposureConfiguration.V1Config,
        token: String
    ) {
        TODO("Not yet implemented")
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