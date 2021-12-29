package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import com.google.android.gms.nearby.exposurenotification.ReportType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.google.android.gms.nearby.exposurenotification.DailySummary as NativeDailySummary
import com.google.android.gms.nearby.exposurenotification.DailySummary.ExposureSummaryData as NativeExposureSummaryData

@Serializable
class DailySummary(
    @SerialName("DateMillisSinceEpoch") val dateMillisSinceEpoch: Long,
    @SerialName("DaySummary") val summaryData: ExposureSummaryData?,
    @SerialName("ConfirmedClinicalDiagnosisSummary") val confirmedClinicalDiagnosisSummary: ExposureSummaryData?,
    @SerialName("ConfirmedTestSummary") val confirmedTestSummary: ExposureSummaryData?,
    @SerialName("RecursiveSummary") val recursiveSummary: ExposureSummaryData?,
    @SerialName("SelfReportedSummary") val selfReportedSummary: ExposureSummaryData?,
) {
    constructor(
        dailySummary: NativeDailySummary
    ) : this(
        dateMillisSinceEpoch = dailySummary.daysSinceEpoch * 24 * 60 * 60 * 1000L,
        summaryData = ExposureSummaryData(dailySummary.summaryData),
        confirmedClinicalDiagnosisSummary = ExposureSummaryData(
            dailySummary.getSummaryDataForReportType(
                ReportType.CONFIRMED_CLINICAL_DIAGNOSIS
            )
        ),
        confirmedTestSummary = ExposureSummaryData(
            dailySummary.getSummaryDataForReportType(
                ReportType.CONFIRMED_TEST
            )
        ),
        recursiveSummary = ExposureSummaryData(dailySummary.getSummaryDataForReportType(ReportType.RECURSIVE)),
        selfReportedSummary = ExposureSummaryData(
            dailySummary.getSummaryDataForReportType(
                ReportType.SELF_REPORT
            )
        ),
    )
}

@Serializable
data class ExposureSummaryData(
    @SerialName("MaximumScore") val maximumScore: Double,
    @SerialName("ScoreSum") val scoreSum: Double,
    @SerialName("WeightedDurationSum") val weightedDurationSum: Double,
) {
    constructor(
        exposureSummaryData: NativeExposureSummaryData
    ) : this(
        maximumScore = exposureSummaryData.maximumScore,
        scoreSum = exposureSummaryData.scoreSum,
        weightedDurationSum = exposureSummaryData.weightedDurationSum,
    )
}
