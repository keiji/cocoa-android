package dev.keiji.cocoa.android.exposure_notification.repository

import android.content.Context
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.RiskEvent
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.RiskLevel
import dev.keiji.cocoa.android.exposure_notification.dao.DailySummaryDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureInformationDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureSummaryDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureWindowDao
import dev.keiji.cocoa.android.exposure_notification.model.DailySummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureInformationModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureSummaryDataModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowAndScanInstancesModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import timber.log.Timber
import java.util.*

interface RiskEventRepository {
    suspend fun findAll(): List<RiskEvent>
    fun calculateRiskLevel(dailySummaryModel: DailySummaryModel): RiskLevel
    fun calculateRiskLevel(exposureInformation: ExposureInformationModel): RiskLevel
}

class RiskEventRepositoryImpl(
    applicationContext: Context,
    private val exposureSummaryDao: ExposureSummaryDao,
    private val exposureInformationDao: ExposureInformationDao,
    private val dailySummaryDao: DailySummaryDao,
    private val exposureWindowDao: ExposureWindowDao,
) : RiskEventRepository {

    override suspend fun findAll(): List<RiskEvent> = withContext(Dispatchers.IO) {
        val riskEventList: MutableList<RiskEvent> = mutableListOf()

        val dailySummaryList = dailySummaryDao.getAll()
        val exposureInformationList = exposureInformationDao.getAll()

        val dailySummaryMap =
            dailySummaryList.groupBy { dailySummaryModel -> dailySummaryModel.dateMillisSinceEpoch }
        val exposureInformationMap =
            exposureInformationList.groupBy { exposureInformation -> exposureInformation.dateMillisSinceEpoch }

        val dateMillisSinceEpochList = dailySummaryMap.keys.union(exposureInformationMap.keys)

        dateMillisSinceEpochList.forEach { dateMillisSinceEpoch ->

            val riskEvent = RiskEvent(
                dateTime = DateTime(dateMillisSinceEpoch, DateTimeZone.UTC),
            )
            dailySummaryMap[dateMillisSinceEpoch]?.also { dailySummaryModelList ->
                dailySummaryModelList.forEach { dailySummaryModel ->
                    val exposureWindowList = exposureWindowDao.findByExact(dailySummaryModel.dateMillisSinceEpoch)
                    val exposureInSeconds = calculateExposureSeconds(exposureWindowList)

                    val riskLevel = calculateRiskLevel(dailySummaryModel)
                    riskEvent.also {
                        it.exposureWIndowRiskLevel = riskLevel
                        it.exposureInSeconds = exposureInSeconds
                    }
                }
            }

            exposureInformationMap[dateMillisSinceEpoch]?.also { exposureInformationList ->
                val highRiskExposureInformationList =
                    exposureInformationList.filter { exposureInformation ->
                        calculateRiskLevel(exposureInformation) > RiskLevel.RISK_LEVEL_MEDIUM
                    }
                val riskLevel =
                    highRiskExposureInformationList.maxOfOrNull { calculateRiskLevel(it) }
                        ?: return@forEach

                riskEvent.also {
                    it.exposureWIndowRiskLevel = riskLevel
                    it.legacyV1Count = highRiskExposureInformationList.size
                }
            }

            riskEventList.add(riskEvent)
        }

        return@withContext riskEventList.sortedDescending()
    }

    private fun calculateExposureSeconds(exposureWindowList: List<ExposureWindowAndScanInstancesModel>): Int {
        return exposureWindowList.sumOf { it.scanInstances.sumOf { it.secondsSinceLastScan } }
    }

    override fun calculateRiskLevel(exposureInformation: ExposureInformationModel): RiskLevel {
        val totalRiskScore = exposureInformation.totalRiskScore
        return when {
            totalRiskScore > 40 -> RiskLevel.RISK_LEVEL_HIGHEST
            totalRiskScore > 35 -> RiskLevel.RISK_LEVEL_HIGH
            totalRiskScore > 30 -> RiskLevel.RISK_LEVEL_MEDIUM_HIGH
            totalRiskScore > 25 -> RiskLevel.RISK_LEVEL_MEDIUM
            totalRiskScore > 20 -> RiskLevel.RISK_LEVEL_LOW_MEDIUM
            totalRiskScore > 10 -> RiskLevel.RISK_LEVEL_LOW
            totalRiskScore > 5 -> RiskLevel.RISK_LEVEL_LOWEST
            else -> RiskLevel.RISK_LEVEL_INVALID
        }
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
