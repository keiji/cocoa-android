package dev.keiji.cocoa.android.exposure_notification.exposure_detection

import android.os.SystemClock
import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.DailySummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureInformation
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureSummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureWindow
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataBaseModel
import dev.keiji.cocoa.android.exposure_notification.repository.ExposureDataRepository
import timber.log.Timber

/**
 * TODO: Change name more appropriately.
 */
interface ExposureResultService {
    suspend fun onNoExposureDetected()

    suspend fun onExposureDetected(
        exposureSummary: ExposureSummary? = null,
        exposureInformationList: List<ExposureInformation> = listOf(),
        dailySummaryList: List<DailySummary> = listOf(),
        exposureWindowList: List<ExposureWindow> = listOf(),
    )
}

class ExposureResultServiceImpl(
    private val dateTimeSource: DateTimeSource,
    private val exposureDataRepository: ExposureDataRepository,
    private val localNotificationManager: LocalNotificationManager,
) : ExposureResultService {
    companion object {
        private const val TIMEOUT_INTERVAL_IN_MILLIS = 1000 * 60 * 60 * 2
    }

    override suspend fun onNoExposureDetected() {
        val epochInMillis = dateTimeSource.epochInMillis()

        Timber.d("onNoExposureDetected ${dateTimeSource.epochInMillis()}")

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

        exposureDataResultReceived.exposureBaseData.also { exposureBaseData ->
            exposureBaseData.finishedEpoch = dateTimeSource.epochInMillis()
            exposureBaseData.state = ExposureDataBaseModel.State.Finished
            Timber.d(
                exposureDataResultReceived.exposureBaseData.region +
                        "-${exposureDataResultReceived.exposureBaseData.subregionList}," +
                        " Elapsed: ${SystemClock.uptimeMillis() - exposureBaseData.startUptime}"
            )
        }

        exposureDataRepository.upsert(
            exposureBaseData = exposureDataResultReceived.exposureBaseData,
            diagnosisKeysFileList = exposureDataResultReceived.diagnosisKeysFileList,
        )

        Timber.d("finished: onNoExposureDetected ${dateTimeSource.epochInMillis()}")
    }

    override suspend fun onExposureDetected(
        exposureSummary: ExposureSummary?,
        exposureInformationList: List<ExposureInformation>,
        dailySummaryList: List<DailySummary>,
        exposureWindowList: List<ExposureWindow>,
    ) {
        Timber.d("started: onExposureDetected ${dateTimeSource.epochInMillis()}")

        val epochInMillis = dateTimeSource.epochInMillis()
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

        exposureDataResultReceived.exposureBaseData.also { exposureBaseData ->
            exposureBaseData.finishedEpoch = dateTimeSource.epochInMillis()
            exposureBaseData.state = ExposureDataBaseModel.State.Finished
            Timber.d(
                exposureDataResultReceived.exposureBaseData.region +
                        "-${exposureDataResultReceived.exposureBaseData.subregionList}," +
                        " Elapsed: ${SystemClock.uptimeMillis() - exposureBaseData.startUptime}"
            )
        }

        exposureDataRepository.upsert(
            exposureBaseData = exposureDataResultReceived.exposureBaseData,
            diagnosisKeysFileList = exposureDataResultReceived.diagnosisKeysFileList,
            exposureSummary = exposureSummary,
            exposureInformationList = exposureInformationList,
            dailySummaryList = dailySummaryList,
            exposureWindowList = exposureWindowList
        )

        localNotificationManager.notifyDetectExposureHighRisk()

        Timber.d("finished: onExposureDetected ${dateTimeSource.epochInMillis()}")
    }
}
