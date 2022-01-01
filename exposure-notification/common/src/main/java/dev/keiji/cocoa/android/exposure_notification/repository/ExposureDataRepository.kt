package dev.keiji.cocoa.android.exposure_notification.repository

import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.DailySummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureInformation
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureSummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureWindow
import dev.keiji.cocoa.android.exposure_notification.dao.DailySummaryDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureDataDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureInformationDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureSummaryDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureWindowDao
import dev.keiji.cocoa.android.exposure_notification.model.DailySummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.DiagnosisKeysFileModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataBaseModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureInformationModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureSummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowModelAndScanInstances
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

interface ExposureDataRepository {
    suspend fun save(
        baseData: ExposureDataBaseModel,
        diagnosisKeysFileList: List<DiagnosisKeysFileModel>,
        exposureSummary: ExposureSummary,
        exposureInformationList: List<ExposureInformation>,
        dailySummaryList: List<DailySummary>,
        exposureWindowList: List<ExposureWindow>
    )

    suspend fun loadExposureDataAll(): List<ExposureDataModel>

    suspend fun findExposureInformationListBy(fromDate: DateTime): List<ExposureInformationModel>

    suspend fun findDailySummaryListBy(fromDate: DateTime): List<DailySummaryModel>
    suspend fun findDExposureWindowListBy(fromDate: DateTime): List<ExposureWindowModelAndScanInstances>
}

class ExposureDataRepositoryImpl(
    private val pathSource: PathSource,
    private val dateTimeSource: DateTimeSource,
    private val exposureDataDao: ExposureDataDao,
    private val exposureInformationDao: ExposureInformationDao,
    private val dailySummaryDao: DailySummaryDao,
    private val exposureWindowDao: ExposureWindowDao,
) : ExposureDataRepository {

    override suspend fun save(
        baseData: ExposureDataBaseModel,
        diagnosisKeysFileList: List<DiagnosisKeysFileModel>,
        exposureSummary: ExposureSummary,
        exposureInformationList: List<ExposureInformation>,
        dailySummaryList: List<DailySummary>,
        exposureWindowList: List<ExposureWindow>
    ) = withContext(Dispatchers.IO) {
        val id = exposureDataDao.insert(
            exposureBaseData = baseData,
            diagnosisKeysFileList = diagnosisKeysFileList,
            exposureSummary = ExposureSummaryModel(exposureSummary),
            exposureInformationList = exposureInformationList
                .map { obj -> ExposureInformationModel(obj) },
            dailySummaryList = dailySummaryList
                .map { obj -> DailySummaryModel(obj) },
            exposureWindowList = exposureWindowList
                .map { obj -> ExposureWindowModelAndScanInstances(obj) },
        )
    }

    override suspend fun loadExposureDataAll(): List<ExposureDataModel> =
        withContext(Dispatchers.IO) {
            return@withContext exposureDataDao.getAll()
        }

    override suspend fun findDailySummaryListBy(fromDate: DateTime): List<DailySummaryModel> =
        withContext(Dispatchers.IO) {
            return@withContext dailySummaryDao.findBy(fromDate.millis)
        }

    override suspend fun findDExposureWindowListBy(fromDate: DateTime): List<ExposureWindowModelAndScanInstances> =
        withContext(Dispatchers.IO) {
            return@withContext exposureWindowDao.findBy(fromDate.millis)
        }

    override suspend fun findExposureInformationListBy(fromDate: DateTime): List<ExposureInformationModel> =
        withContext(Dispatchers.IO) {
            return@withContext exposureInformationDao.findBy(fromDate.millis)
        }
}
