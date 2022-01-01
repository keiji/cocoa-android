package dev.keiji.cocoa.android.exposure_notification.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.DailySummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureSummaryData

@Entity(tableName = "daily_summaries")
class DailySummaryModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "exposure_data_id")
    var exposureDataId: Long,

    @ColumnInfo(name = "date_millis_since_epoch")
    val dateMillisSinceEpoch: Long,

    @Embedded(prefix = "summary_data_")
    val summaryData: ExposureSummaryDataModel?,

    @Embedded(prefix = "confirmed_clinical_diagnosis_summary_data_")
    val confirmedClinicalDiagnosisSummary: ExposureSummaryDataModel?,

    @Embedded(prefix = "confirmed_test_summary_data_")
    val confirmedTestSummary: ExposureSummaryDataModel?,

    @Embedded(prefix = "recursive_summary_data_")
    val recursiveSummary: ExposureSummaryDataModel?,

    @Embedded(prefix = "self_reported_summary_data_")
    val selfReportedSummary: ExposureSummaryDataModel?,
) {
    companion object {
        fun createExposureSummaryDataModel(exposureSummaryData: ExposureSummaryData?): ExposureSummaryDataModel? {
            exposureSummaryData ?: return null
            return ExposureSummaryDataModel(exposureSummaryData)
        }
    }

    constructor(
        dailySummary: DailySummary
    ) : this(
        id = 0,
        exposureDataId = 0,
        dateMillisSinceEpoch = dailySummary.dateMillisSinceEpoch,
        summaryData = createExposureSummaryDataModel(dailySummary.summaryData),
        confirmedClinicalDiagnosisSummary = createExposureSummaryDataModel(dailySummary.confirmedClinicalDiagnosisSummary),
        confirmedTestSummary = createExposureSummaryDataModel(dailySummary.confirmedTestSummary),
        recursiveSummary = createExposureSummaryDataModel(dailySummary.recursiveSummary),
        selfReportedSummary = createExposureSummaryDataModel(dailySummary.selfReportedSummary),
    )
}

data class ExposureSummaryDataModel(
    @ColumnInfo(name = "maximum_score") val maximumScore: Double,
    @ColumnInfo(name = "score_sum") val scoreSum: Double,
    @ColumnInfo(name = "weighted_duration_sum") val weightedDurationSum: Double,
) {
    constructor(
        exposureSummaryData: ExposureSummaryData
    ) : this(
        maximumScore = exposureSummaryData.maximumScore,
        scoreSum = exposureSummaryData.scoreSum,
        weightedDurationSum = exposureSummaryData.weightedDurationSum,
    )
}
