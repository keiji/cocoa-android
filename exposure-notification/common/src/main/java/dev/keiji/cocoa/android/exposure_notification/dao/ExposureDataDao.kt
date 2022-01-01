package dev.keiji.cocoa.android.exposure_notification.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import dev.keiji.cocoa.android.exposure_notification.model.DailySummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.DiagnosisKeysFileModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataBaseModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureInformationModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureSummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowAndScanInstancesModel
import dev.keiji.cocoa.android.exposure_notification.model.ScanInstanceModel

@Dao
abstract class ExposureDataDao {

    @Transaction
    @Query("SELECT * FROM exposure_data")
    abstract suspend fun getAll(): List<ExposureDataModel>

    @Insert
    abstract suspend fun insert(exposureDataBaseModel: ExposureDataBaseModel): Long

    @Insert
    abstract suspend fun insertAll(exposureDataModelList: List<ExposureDataBaseModel>): List<Long>

    @Transaction
    @Insert
    open suspend fun insert(
        exposureBaseData: ExposureDataBaseModel,
        diagnosisKeysFileList: List<DiagnosisKeysFileModel> = emptyList(),
        exposureSummary: ExposureSummaryModel? = null,
        exposureInformationList: List<ExposureInformationModel> = emptyList(),
        dailySummaryList: List<DailySummaryModel> = emptyList(),
        exposureWindowList: List<ExposureWindowAndScanInstancesModel> = emptyList(),
    ): ExposureDataModel {
        val exposureDataId = insert(exposureBaseData)

        diagnosisKeysFileList.forEach { model ->
            model.exposureDataId = exposureDataId
        }
        insertDiagnosisKeysFileList(diagnosisKeysFileList)

        exposureSummary?.also { model ->
            model.exposureDataId = exposureDataId
            insert(model)
        }
        exposureInformationList.also { modelList ->
            modelList.forEach { model -> model.exposureDataId = exposureDataId }
            insert(modelList)
        }

        dailySummaryList.forEach { model ->
            model.exposureDataId = exposureDataId
        }
        insertDailySummaryList(dailySummaryList)

        exposureWindowList.forEach { model ->
            model.exposureWindowModel.exposureDataId = exposureDataId
            insert(model.exposureWindowModel, model.scanInstances)
        }

        return ExposureDataModel(
            exposureBaseData = exposureBaseData,
            diagnosisKeysFileList = diagnosisKeysFileList,
            dailySummaryList = dailySummaryList,
            exposureWindowList = exposureWindowList,
            exposureSummary = exposureSummary,
            exposureInformationList = exposureInformationList,
        )
    }

    @Insert
    abstract suspend fun insertDiagnosisKeysFileList(
        diagnosisKeysFileList: List<DiagnosisKeysFileModel>
    ): List<Long>

    @Insert
    abstract suspend fun insert(dailySummaryModel: DailySummaryModel): Long

    @Insert
    abstract suspend fun insertDailySummaryList(
        dailySummaryList: List<DailySummaryModel>
    ): List<Long>

    @Insert
    abstract suspend fun insert(exposureWindowModel: ExposureWindowModel): Long

    @Insert
    abstract suspend fun insertScanInstances(
        scanInstanceModelList: List<ScanInstanceModel>
    ): List<Long>

    @Transaction
    @Insert
    open suspend fun insert(
        exposureWindowModel: ExposureWindowModel,
        scanInstanceModels: List<ScanInstanceModel>
    ): Long {
        val exposureWindowId = insert(exposureWindowModel)

        scanInstanceModels.forEach { scanInstanceModel ->
            scanInstanceModel.exposureWindowId = exposureWindowId
        }

        insertScanInstances(scanInstanceModels)

        return exposureWindowId
    }

    @Insert
    abstract suspend fun insert(exposureSummaryModel: ExposureSummaryModel): Long

    @Insert
    abstract suspend fun insert(exposureInformationList: List<ExposureInformationModel>): List<Long>

}
