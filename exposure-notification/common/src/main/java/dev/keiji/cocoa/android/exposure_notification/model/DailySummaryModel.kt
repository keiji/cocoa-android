package dev.keiji.cocoa.android.exposure_notification.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.DailySummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureSummaryData
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

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

    val dateTime
        get() = DateTime(dateMillisSinceEpoch, DateTimeZone.UTC)

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DailySummaryModel

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
