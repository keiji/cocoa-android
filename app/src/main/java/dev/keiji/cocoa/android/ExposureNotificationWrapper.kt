package dev.keiji.cocoa.android

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.exposurenotification.DailySummariesConfig
import com.google.android.gms.nearby.exposurenotification.DailySummary
import com.google.android.gms.nearby.exposurenotification.DiagnosisKeyFileProvider
import com.google.android.gms.nearby.exposurenotification.DiagnosisKeysDataMapping
import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatus
import com.google.android.gms.nearby.exposurenotification.ExposureSummary
import com.google.android.gms.nearby.exposurenotification.ExposureWindow
import com.google.android.gms.nearby.exposurenotification.PackageConfiguration
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.File

class ExposureNotificationWrapper(applicationContext: Context) {

    companion object {
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

    suspend fun getStatuses(): Set<ExposureNotificationStatus> =
        exposureNotificationClient.status.await()

    suspend fun getCalibrationConfidence(): Int =
        exposureNotificationClient.calibrationConfidence.await()

    suspend fun getDiagnosisKeysDataMapping(): DiagnosisKeysDataMapping =
        exposureNotificationClient.diagnosisKeysDataMapping.await()

    suspend fun setDiagnosisKeysDataMapping(diagnosisKeysDataMapping: DiagnosisKeysDataMapping) =
        exposureNotificationClient.setDiagnosisKeysDataMapping(diagnosisKeysDataMapping).await()

    suspend fun getPackageConfiguration(): PackageConfiguration =
        exposureNotificationClient.packageConfiguration.await()

    suspend fun getExposureWindow(): List<ExposureWindow> =
        exposureNotificationClient.exposureWindows.await()

    suspend fun getDailySummary(dailySummariesConfig: DailySummariesConfig): List<DailySummary> =
        exposureNotificationClient.getDailySummaries(dailySummariesConfig).await()

    suspend fun getExposureSummary(token: String): ExposureSummary =
        exposureNotificationClient.getExposureSummary(token).await()

    suspend fun getExposureInformation(token: String): List<ExposureInformation> =
        exposureNotificationClient.getExposureInformation(token).await()

    suspend fun getTemporaryExposureKeyHistory(activity: Activity): List<TemporaryExposureKey>? {
        try {
            return exposureNotificationClient.temporaryExposureKeyHistory.await()
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
        exposureConfiguration: ExposureConfiguration,
        token: String
    ) = exposureNotificationClient.provideDiagnosisKeys(
        diagnosisKeyFileList,
        exposureConfiguration,
        token
    ).await()

    suspend fun requestPreAuthorizedTemporaryExposureKeyHistory(activity: Activity) {
        try {
            exposureNotificationClient.requestPreAuthorizedTemporaryExposureKeyHistory().await()
        } catch (exception: ApiException) {
            Log.d(TAG, "${exception.status}")

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

@Module
@InstallIn(SingletonComponent::class)
class ExposureNotificationModule {

    @Provides
    fun provideExposureNotificationWrapper(
        @ApplicationContext applicationContext: Context
    ): ExposureNotificationWrapper {
        return ExposureNotificationWrapper(applicationContext)
    }

}
