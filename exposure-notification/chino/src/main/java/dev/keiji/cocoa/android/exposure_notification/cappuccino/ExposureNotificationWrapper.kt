package dev.keiji.cocoa.android.exposure_notification.cappuccino

import android.app.Activity
import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.DailySummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureConfiguration
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureInformation
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureNotificationStatus
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureSummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureWindow
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.PackageConfiguration
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.TemporaryExposureKey
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.File

interface ExposureNotificationWrapper {
    companion object {
        const val ACTION_EXPOSURE_NOT_FOUND = ExposureNotificationClient.ACTION_EXPOSURE_NOT_FOUND
        const val ACTION_EXPOSURE_STATE_UPDATED =
            ExposureNotificationClient.ACTION_EXPOSURE_STATE_UPDATED
        const val EXTRA_EXPOSURE_SUMMARY = ExposureNotificationClient.EXTRA_EXPOSURE_SUMMARY
        const val EXTRA_TOKEN = ExposureNotificationClient.EXTRA_TOKEN

        const val EXTRA_SERVICE_STATE = ExposureNotificationClient.EXTRA_SERVICE_STATE

        const val REQUEST_EXPOSURE_NOTIFICATION_START = 0x01
        const val REQUEST_EXPOSURE_NOTIFICATION_STOP = 0x02
        const val REQUEST_TEMPORARY_EXPOSURE_KEY_HISTORY = 0x03
    }

    suspend fun start(activity: Activity)
    suspend fun stop(activity: Activity)

    suspend fun getVersion(): Long
    suspend fun isEnabled(): Boolean
    suspend fun getStatuses(): List<ExposureNotificationStatus>
    suspend fun getCalibrationConfidence(): Int
    suspend fun getDiagnosisKeysDataMapping(): ExposureConfiguration.DiagnosisKeysDataMappingConfig
    suspend fun setDiagnosisKeysDataMapping(diagnosisKeysDataMapping: ExposureConfiguration.DiagnosisKeysDataMappingConfig)
    suspend fun getPackageConfiguration(): PackageConfiguration
    suspend fun getExposureWindow(): List<ExposureWindow>
    suspend fun getDailySummary(dailySummariesConfig: ExposureConfiguration.DailySummariesConfig): List<DailySummary>
    suspend fun getExposureSummary(token: String): ExposureSummary
    suspend fun getExposureInformation(token: String): List<ExposureInformation>
    suspend fun getTemporaryExposureKeyHistory(activity: Activity): List<TemporaryExposureKey>?

    suspend fun provideDiagnosisKeys(diagnosisKeyFileList: List<File>)
    suspend fun provideDiagnosisKeys(diagnosisKeyFileProvider: DiagnosisKeyFileProvider)
    suspend fun provideDiagnosisKeys(
        diagnosisKeyFileList: List<File>,
        exposureConfiguration: ExposureConfiguration.V1Config,
        token: String
    )

    suspend fun requestPreAuthorizedTemporaryExposureKeyHistory(activity: Activity)
    suspend fun requestPreAuthorizedTemporaryExposureKeyRelease()
    suspend fun requestPreAuthorizedTemporaryExposureKeyHistoryForSelfReport()
}

class ExposureNotificationWrapperImpl(applicationContext: Context) : ExposureNotificationWrapper {

    companion object {
        private val TAG = ExposureNotificationWrapper::class.java.simpleName
    }

    private val exposureNotificationClient =
        Nearby.getExposureNotificationClient(applicationContext)

    override suspend fun start(activity: Activity) {
        try {
            exposureNotificationClient.start().await()
        } catch (exception: ApiException) {
            Timber.d("ApiException", exception)

            if (exception.status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                exception.status.startResolutionForResult(
                    activity,
                    ExposureNotificationWrapper.REQUEST_EXPOSURE_NOTIFICATION_START
                )
            } else {
                throw exception.toExposureNotificationException()
            }
        }
    }

    override suspend fun stop(activity: Activity) {
        try {
            exposureNotificationClient.stop().await()
        } catch (exception: ApiException) {
            Timber.d("ApiException", exception)

            if (exception.status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                exception.status.startResolutionForResult(
                    activity,
                    ExposureNotificationWrapper.REQUEST_EXPOSURE_NOTIFICATION_STOP
                )
            } else {
                throw exception.toExposureNotificationException()
            }
        }
    }

    override suspend fun getVersion(): Long =
        exposureNotificationClient.version.await()

    override suspend fun isEnabled(): Boolean =
        exposureNotificationClient.isEnabled.await()

    override suspend fun getStatuses(): List<ExposureNotificationStatus> =
        exposureNotificationClient.status.await()
            .map { status -> status.toExposureNotificationStatus() }

    override suspend fun getCalibrationConfidence(): Int =
        exposureNotificationClient.calibrationConfidence.await()

    override suspend fun getDiagnosisKeysDataMapping(): ExposureConfiguration.DiagnosisKeysDataMappingConfig {
        val nativeDiagnosisKeysDataMapping =
            exposureNotificationClient.diagnosisKeysDataMapping.await()

        return ExposureConfiguration.DiagnosisKeysDataMappingConfig(
            infectiousnessWhenDaysSinceOnsetMissing = nativeDiagnosisKeysDataMapping.infectiousnessWhenDaysSinceOnsetMissing,
            reportTypeWhenMissing = nativeDiagnosisKeysDataMapping.reportTypeWhenMissing,
        ).also { diagnosisKeysDataMappingConfig ->
            nativeDiagnosisKeysDataMapping.daysSinceOnsetToInfectiousness.entries.forEach { (key, value) ->
                diagnosisKeysDataMappingConfig.daysSinceOnsetToInfectiousness[key] = value
            }
        }
    }

    override suspend fun setDiagnosisKeysDataMapping(diagnosisKeysDataMapping: ExposureConfiguration.DiagnosisKeysDataMappingConfig) {
        exposureNotificationClient.setDiagnosisKeysDataMapping(diagnosisKeysDataMapping.toNative())
            .await()
    }

    override suspend fun getPackageConfiguration(): PackageConfiguration {
        val nativePackageConfiguration = exposureNotificationClient.packageConfiguration.await()
        return PackageConfiguration(nativePackageConfiguration.values)
    }

    override suspend fun getExposureWindow(): List<ExposureWindow> =
        exposureNotificationClient.exposureWindows.await().map { ew -> ExposureWindow(ew) }

    override suspend fun getDailySummary(dailySummariesConfig: ExposureConfiguration.DailySummariesConfig): List<DailySummary> =
        exposureNotificationClient.getDailySummaries(dailySummariesConfig.toNative()).await()
            .map { ds -> DailySummary(ds) }

    override suspend fun getExposureSummary(token: String): ExposureSummary =
        ExposureSummary(exposureNotificationClient.getExposureSummary(token).await())

    override suspend fun getExposureInformation(token: String): List<ExposureInformation> =
        exposureNotificationClient.getExposureInformation(token).await().map { ei ->
            ExposureInformation(ei)
        }

    override suspend fun getTemporaryExposureKeyHistory(activity: Activity): List<TemporaryExposureKey>? {
        try {
            return exposureNotificationClient.temporaryExposureKeyHistory.await()
                ?.map { tek -> TemporaryExposureKey(tek) }
        } catch (exception: ApiException) {
            Timber.d("ApiException", exception)

            if (exception.status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                exception.status.startResolutionForResult(
                    activity,
                    ExposureNotificationWrapper.REQUEST_TEMPORARY_EXPOSURE_KEY_HISTORY
                )
            }
        }
        return null
    }

    override suspend fun provideDiagnosisKeys(diagnosisKeyFileList: List<File>) {
        provideDiagnosisKeys(DiagnosisKeyFileProvider(diagnosisKeyFileList))
    }

    override suspend fun provideDiagnosisKeys(diagnosisKeyFileProvider: DiagnosisKeyFileProvider) {
        exposureNotificationClient.provideDiagnosisKeys(diagnosisKeyFileProvider.toNative()).await()
    }

    override suspend fun provideDiagnosisKeys(
        diagnosisKeyFileList: List<File>,
        exposureConfiguration: ExposureConfiguration.V1Config,
        token: String
    ) {
        exposureNotificationClient.provideDiagnosisKeys(
            diagnosisKeyFileList,
            exposureConfiguration.toNative(),
            token
        ).await()
    }

    override suspend fun requestPreAuthorizedTemporaryExposureKeyHistory(activity: Activity) {
        try {
            exposureNotificationClient.requestPreAuthorizedTemporaryExposureKeyHistory().await()
        } catch (exception: ApiException) {
            Timber.d("${exception.status}")

            if (exception.status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                exception.status.startResolutionForResult(
                    activity,
                    ExposureNotificationWrapper.REQUEST_EXPOSURE_NOTIFICATION_STOP
                )
            }
        }
    }

    override suspend fun requestPreAuthorizedTemporaryExposureKeyRelease() {
        exposureNotificationClient.requestPreAuthorizedTemporaryExposureKeyRelease().await()
    }

    override suspend fun requestPreAuthorizedTemporaryExposureKeyHistoryForSelfReport() {
        exposureNotificationClient.requestPreAuthorizedTemporaryExposureKeyHistoryForSelfReport()
            .await()
    }
}
