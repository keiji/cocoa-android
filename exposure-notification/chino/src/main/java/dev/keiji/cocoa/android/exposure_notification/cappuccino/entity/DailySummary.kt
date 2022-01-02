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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DailySummary

        if (dateMillisSinceEpoch != other.dateMillisSinceEpoch) return false
        if (summaryData != other.summaryData) return false
        if (confirmedClinicalDiagnosisSummary != other.confirmedClinicalDiagnosisSummary) return false
        if (confirmedTestSummary != other.confirmedTestSummary) return false
        if (recursiveSummary != other.recursiveSummary) return false
        if (selfReportedSummary != other.selfReportedSummary) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dateMillisSinceEpoch.hashCode()
        result = 31 * result + (summaryData?.hashCode() ?: 0)
        result = 31 * result + (confirmedClinicalDiagnosisSummary?.hashCode() ?: 0)
        result = 31 * result + (confirmedTestSummary?.hashCode() ?: 0)
        result = 31 * result + (recursiveSummary?.hashCode() ?: 0)
        result = 31 * result + (selfReportedSummary?.hashCode() ?: 0)
        return result
    }
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
