package dev.keiji.cocoa.android.exposure_notification.repository

import android.content.Context
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.RiskEvent
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.RiskLevel
import dev.keiji.cocoa.android.exposure_notification.dao.DailySummaryDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureWindowDao
import dev.keiji.cocoa.android.exposure_notification.model.DailySummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureSummaryDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import timber.log.Timber
import java.util.*

interface RiskEventRepository {
    suspend fun findAll(): List<RiskEvent>
    fun calculateRiskLevel(dailySummaryModel: DailySummaryModel): RiskLevel
}

class RiskEventRepositoryImpl(
    applicationContext: Context,
    private val dailySummaryDao: DailySummaryDao,
    private val exposureWindowDao: ExposureWindowDao,
) : RiskEventRepository {

    override suspend fun findAll(): List<RiskEvent> = withContext(Dispatchers.IO) {
        val riskEventList: MutableList<RiskEvent> = mutableListOf()

        val dailySummaryList = dailySummaryDao.getAll()

        dailySummaryList.forEach { dailySummaryModel ->
            val exposureWindowList =
                exposureWindowDao.findByExact(dailySummaryModel.dateMillisSinceEpoch)

            val riskLevel = calculateRiskLevel(dailySummaryModel)
            val riskEvent = RiskEvent(
                dailySummaryModel.dateTime.toDateTime(DateTimeZone.getDefault()),
                riskLevel,
                exposureWindowList.size
            )
            riskEventList.add(riskEvent)
        }

        return@withContext riskEventList.sortedDescending()
    }

    override fun calculateRiskLevel(dailySummaryModel: DailySummaryModel): RiskLevel {
        var riskPoint = 0.0

        dailySummaryModel.summaryData?.also { summaryData ->
            riskPoint += calculateRiskPoint(summaryData, 1.0)
        }
        dailySummaryModel.confirmedTestSummary?.also { summaryData ->
            riskPoint += calculateRiskPoint(summaryData, 1.5)
        }
        dailySummaryModel.confirmedClinicalDiagnosisSummary?.also { summaryData ->
            riskPoint += calculateRiskPoint(summaryData, 1.2)
        }
        dailySummaryModel.recursiveSummary?.also { summaryData ->
            riskPoint += calculateRiskPoint(summaryData, 0.5)
        }
        dailySummaryModel.selfReportedSummary?.also { summaryData ->
            riskPoint += calculateRiskPoint(summaryData, 0.5)
        }

        Timber.d("Risk point = $riskPoint")

        return when {
            riskPoint > 20 -> RiskLevel.RISK_LEVEL_HIGHEST
            riskPoint > 18 -> RiskLevel.RISK_LEVEL_HIGH
            riskPoint > 14 -> RiskLevel.RISK_LEVEL_MEDIUM_HIGH
            riskPoint > 12 -> RiskLevel.RISK_LEVEL_MEDIUM
            riskPoint > 10 -> RiskLevel.RISK_LEVEL_LOW_MEDIUM
            riskPoint > 8 -> RiskLevel.RISK_LEVEL_LOW
            riskPoint > 5 -> RiskLevel.RISK_LEVEL_LOWEST
            else -> RiskLevel.RISK_LEVEL_INVALID
        }
    }

    private fun calculateRiskPoint(
        summaryData: ExposureSummaryDataModel,
        weight: Double = 1.0
    ): Double {
        var riskPoint = 0.0

        if (summaryData.maximumScore > 2000) {
            riskPoint += 10 * weight
        } else if (summaryData.maximumScore > 1000) {
            riskPoint += 5 * weight
        }
        if (summaryData.scoreSum > 10000) {
            riskPoint += 10 * weight
        } else if (summaryData.scoreSum > 5000) {
            riskPoint += 5 * weight
        } else if (summaryData.weightedDurationSum > 10000) {
            riskPoint += 10 * weight
        } else if (summaryData.weightedDurationSum > 5000) {
            riskPoint += 5 * weight
        }
        return riskPoint
    }
}
