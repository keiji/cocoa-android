package dev.keiji.cocoa.android.exposure_notification.exposure_detection

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
) : ExposureResultService {
    companion object {
        private const val TIMEOUT_INTERVAL_IN_MILLIS = 1000 * 60 * 60 * 2
    }

    override suspend fun onExposureDetected(
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
}
