package dev.keiji.cocoa.android.exposure_notification.cappuccino

import android.app.Activity
import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.exposurenotification.DiagnosisKeyFileProvider
import com.google.android.gms.nearby.exposurenotification.DiagnosisKeysDataMapping
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.android.gms.nearby.exposurenotification.PackageConfiguration
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.DailySummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureConfiguration
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureInformation
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureNotificationStatus
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureSummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureWindow
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.TemporaryExposureKey
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.File

class ExposureNotificationWrapper(applicationContext: Context) {

    companion object {

        const val ACTION_EXPOSURE_NOT_FOUND = ExposureNotificationClient.ACTION_EXPOSURE_NOT_FOUND
        const val ACTION_EXPOSURE_STATE_UPDATED =
            ExposureNotificationClient.ACTION_EXPOSURE_STATE_UPDATED
        const val EXTRA_EXPOSURE_SUMMARY = ExposureNotificationClient.EXTRA_EXPOSURE_SUMMARY
        const val EXTRA_TOKEN = ExposureNotificationClient.EXTRA_TOKEN

        const val EXTRA_SERVICE_STATE = ExposureNotificationClient.EXTRA_SERVICE_STATE

        private val TAG = ExposureNotificationWrapper::class.java.simpleName

        const val REQUEST_EXPOSURE_NOTIFICATION_START = 0x01
        const val REQUEST_EXPOSURE_NOTIFICATION_STOP = 0x02
        const val REQUEST_TEMPORARY_EXPOSURE_KEY_HISTORY = 0x03
    }

    private val exposureNotificationClient =
        Nearby.getExposureNotificationClient(applicationContext)

    suspend fun start(activity: Activity) {
        try {
            exposureNotificationClient.start().await()
        } catch (exception: ApiException) {
            Timber.d("ApiException", exception)

            if (exception.status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                exception.status.startResolutionForResult(
                    activity,
                    REQUEST_EXPOSURE_NOTIFICATION_START
                )
            }
        }
    }

    suspend fun stop(activity: Activity) {
        try {
            exposureNotificationClient.stop().await()
        } catch (exception: ApiException) {
            Timber.d("ApiException", exception)

            if (exception.status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                exception.status.startResolutionForResult(
                    activity,
                    REQUEST_EXPOSURE_NOTIFICATION_STOP
                )
            }
        }
    }

    suspend fun getVersion(): Long =
        exposureNotificationClient.version.await()

    suspend fun isEnabled(): Boolean =
        exposureNotificationClient.isEnabled.await()

    suspend fun getStatuses(): List<ExposureNotificationStatus> =
        exposureNotificationClient.status.await()
            .map { status -> status.toExposureNotificationStatus() }

    suspend fun getCalibrationConfidence(): Int =
        exposureNotificationClient.calibrationConfidence.await()

    suspend fun getDiagnosisKeysDataMapping(): DiagnosisKeysDataMapping =
        exposureNotificationClient.diagnosisKeysDataMapping.await()

    suspend fun setDiagnosisKeysDataMapping(diagnosisKeysDataMapping: DiagnosisKeysDataMapping) =
        exposureNotificationClient.setDiagnosisKeysDataMapping(diagnosisKeysDataMapping).await()

    suspend fun getPackageConfiguration(): PackageConfiguration =
        exposureNotificationClient.packageConfiguration.await()

    suspend fun getExposureWindow(): List<ExposureWindow> =
        exposureNotificationClient.exposureWindows.await().map { ew -> ExposureWindow(ew) }

    suspend fun getDailySummary(dailySummariesConfig: ExposureConfiguration.DailySummariesConfig): List<DailySummary> =
        exposureNotificationClient.getDailySummaries(dailySummariesConfig.toNative()).await()
            .map { ds -> DailySummary(ds) }

    suspend fun getExposureSummary(token: String): ExposureSummary =
        ExposureSummary(exposureNotificationClient.getExposureSummary(token).await())

    suspend fun getExposureInformation(token: String): List<ExposureInformation> =
        exposureNotificationClient.getExposureInformation(token).await().map { ei ->
            ExposureInformation(ei)
        }

    suspend fun getTemporaryExposureKeyHistory(activity: Activity): List<TemporaryExposureKey>? {
        try {
            return exposureNotificationClient.temporaryExposureKeyHistory.await()
                ?.map { tek -> TemporaryExposureKey(tek) }
        } catch (exception: ApiException) {
            Timber.d("ApiException", exception)

            if (exception.status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                exception.status.startResolutionForResult(
                    activity,
                    REQUEST_TEMPORARY_EXPOSURE_KEY_HISTORY
                )
            }
        }
        return null
    }

    suspend fun provideDiagnosisKeys(diagnosisKeyFileList: List<File>) =
        provideDiagnosisKeys(DiagnosisKeyFileProvider(diagnosisKeyFileList))

    suspend fun provideDiagnosisKeys(diagnosisKeyFileProvider: DiagnosisKeyFileProvider) =
        exposureNotificationClient.provideDiagnosisKeys(diagnosisKeyFileProvider).await()

    suspend fun provideDiagnosisKeys(
        diagnosisKeyFileList: List<File>,
        exposureConfiguration: ExposureConfiguration.V1Config,
        token: String
    ) = exposureNotificationClient.provideDiagnosisKeys(
        diagnosisKeyFileList,
        exposureConfiguration.toNative(),
        token
    ).await()

    suspend fun requestPreAuthorizedTemporaryExposureKeyHistory(activity: Activity) {
        try {
            exposureNotificationClient.requestPreAuthorizedTemporaryExposureKeyHistory().await()
        } catch (exception: ApiException) {
            Timber.d("${exception.status}")

            if (exception.status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                exception.status.startResolutionForResult(
                    activity,
                    REQUEST_EXPOSURE_NOTIFICATION_STOP
                )
            }
        }
    }

    suspend fun requestPreAuthorizedTemporaryExposureKeyRelease() =
        exposureNotificationClient.requestPreAuthorizedTemporaryExposureKeyRelease().await()

    suspend fun requestPreAuthorizedTemporaryExposureKeyHistoryForSelfReport() =
        exposureNotificationClient.requestPreAuthorizedTemporaryExposureKeyHistoryForSelfReport()
            .await()
}
