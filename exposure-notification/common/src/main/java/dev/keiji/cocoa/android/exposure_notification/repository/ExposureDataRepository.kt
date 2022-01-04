package dev.keiji.cocoa.android.exposure_notification.repository

import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.DailySummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureInformation
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureSummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureWindow
import dev.keiji.cocoa.android.exposure_notification.dao.DailySummaryDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureDataDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureInformationDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureWindowDao
import dev.keiji.cocoa.android.exposure_notification.model.DailySummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.DiagnosisKeysFileModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataBaseModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureInformationModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureSummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowAndScanInstancesModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

interface ExposureDataRepository {

    suspend fun upsert(exposureDataModel: ExposureDataModel): Long

    suspend fun upsert(
        exposureBaseData: ExposureDataBaseModel,
        diagnosisKeysFileList: List<DiagnosisKeysFileModel> = listOf(),
        exposureSummary: ExposureSummary? = null,
        exposureInformationList: List<ExposureInformation> = mutableListOf(),
        dailySummaryList: List<DailySummary> = mutableListOf(),
        exposureWindowList: List<ExposureWindow> = mutableListOf()
    ): ExposureDataModel

    suspend fun loadExposureDataAll(): List<ExposureDataModel>

    suspend fun getBy(state: ExposureDataBaseModel.State): ExposureDataModel?

    suspend fun findBy(state: ExposureDataBaseModel.State): List<ExposureDataModel>

    suspend fun findExposureInformationListBy(fromDate: DateTime): List<ExposureInformationModel>

    suspend fun findGroupedExposureInformationListBy(fromDate: DateTime): Map<Long, List<ExposureInformationModel>>

    suspend fun findDailySummaryListBy(fromDate: DateTime): List<DailySummaryModel>

    suspend fun findGroupedDailySummaryListBy(fromDate: DateTime): Map<Long, List<DailySummaryModel>>

    suspend fun findExposureWindowListBy(fromDate: DateTime): List<ExposureWindowAndScanInstancesModel>

    suspend fun findGroupedExposureWindowListBy(fromDate: DateTime): Map<Long, List<ExposureWindowAndScanInstancesModel>>

    suspend fun setTimeout(
        baseTime: Long,
        state: ExposureDataBaseModel.State
    ): List<ExposureDataModel>

    suspend fun cleanupTimeout()
}

class ExposureDataRepositoryImpl(
    private val dateTimeSource: DateTimeSource,
    private val exposureDataDao: ExposureDataDao,
    private val exposureInformationDao: ExposureInformationDao,
    private val dailySummaryDao: DailySummaryDao,
    private val exposureWindowDao: ExposureWindowDao,
) : ExposureDataRepository {

    override suspend fun upsert(exposureDataModel: ExposureDataModel): Long =
        withContext(Dispatchers.IO) {
            val updatedModel = exposureDataDao.upsert(exposureDataModel)
            return@withContext updatedModel.exposureBaseData.id
        }

    override suspend fun upsert(
        exposureBaseData: ExposureDataBaseModel,
        diagnosisKeysFileList: List<DiagnosisKeysFileModel>,
        exposureSummary: ExposureSummary?,
        exposureInformationList: List<ExposureInformation>,
        dailySummaryList: List<DailySummary>,
        exposureWindowList: List<ExposureWindow>
    ) = withContext(Dispatchers.IO) {

        val exposureSummaryModel = if (exposureSummary != null) {
            ExposureSummaryModel(exposureSummary)
        } else {
            null
        }
        return@withContext exposureDataDao.upsert(
            exposureBaseData = exposureBaseData,
            diagnosisKeysFileList = diagnosisKeysFileList,
            exposureSummary = exposureSummaryModel,
            exposureInformationList = exposureInformationList
                .map { obj -> ExposureInformationModel(obj) }
                .toMutableList(),
            dailySummaryList = dailySummaryList
                .map { obj -> DailySummaryModel(obj) }
                .toMutableList(),
            exposureWindowList = exposureWindowList
                .map { obj -> ExposureWindowAndScanInstancesModel(obj) }
                .toMutableList(),
        )
    }

    override suspend fun loadExposureDataAll(): List<ExposureDataModel> =
        withContext(Dispatchers.IO) {
            return@withContext exposureDataDao.getAll()
        }

    override suspend fun getBy(state: ExposureDataBaseModel.State): ExposureDataModel? =
        withContext(Dispatchers.IO) {
            return@withContext exposureDataDao.getBy(state.value)
        }

    override suspend fun findBy(state: ExposureDataBaseModel.State): List<ExposureDataModel> =
        withContext(Dispatchers.IO) {
            return@withContext exposureDataDao.findBy(state.value)
        }

    override suspend fun findDailySummaryListBy(fromDate: DateTime): List<DailySummaryModel> =
        withContext(Dispatchers.IO) {
            return@withContext dailySummaryDao.findBy(fromDate.millis)
        }

    override suspend fun findGroupedDailySummaryListBy(fromDate: DateTime): Map<Long, List<DailySummaryModel>> =
        withContext(Dispatchers.IO) {
            return@withContext dailySummaryDao.findBy(fromDate.millis).groupBy(
                { it.dateMillisSinceEpoch }, { it }
            )
        }

    override suspend fun findExposureWindowListBy(fromDate: DateTime): List<ExposureWindowAndScanInstancesModel> =
        withContext(Dispatchers.IO) {
            return@withContext exposureWindowDao.findByAfter(fromDate.millis)
        }

    override suspend fun findGroupedExposureWindowListBy(fromDate: DateTime): Map<Long, List<ExposureWindowAndScanInstancesModel>> =
        withContext(Dispatchers.IO) {
            return@withContext exposureWindowDao.findByAfter(fromDate.millis).groupBy(
                { it.exposureWindowModel.dateMillisSinceEpoch }, { it }
            )
        }

    override suspend fun setTimeout(
        baseTime: Long,
        state: ExposureDataBaseModel.State
    ): List<ExposureDataModel> =
        withContext(Dispatchers.IO) {
            return@withContext exposureDataDao.setTimeout(baseTime, state.value)
        }

    override suspend fun cleanupTimeout() =
        withContext(Dispatchers.IO) {
            exposureDataDao.cleanup(ExposureDataBaseModel.State.Timeout.value)
        }

    override suspend fun findExposureInformationListBy(fromDate: DateTime): List<ExposureInformationModel> =
        withContext(Dispatchers.IO) {
            return@withContext exposureInformationDao.findBy(fromDate.millis)
        }

    override suspend fun findGroupedExposureInformationListBy(fromDate: DateTime): Map<Long, List<ExposureInformationModel>> =
        withContext(Dispatchers.IO) {
            return@withContext exposureInformationDao.findBy(fromDate.millis).groupBy(
                { it.dateMillisSinceEpoch }, { it }
            )
        }
}
