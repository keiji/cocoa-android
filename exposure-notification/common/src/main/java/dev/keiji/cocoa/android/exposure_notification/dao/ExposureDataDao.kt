package dev.keiji.cocoa.android.exposure_notification.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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
    @Query("SELECT * FROM exposure_data ORDER BY priority DESC")
    abstract suspend fun getAll(): List<ExposureDataModel>

    @Transaction
    @Query("SELECT * FROM exposure_data WHERE state = :stateValue ORDER BY priority DESC LIMIT 1")
    abstract suspend fun getBy(stateValue: Int): ExposureDataModel?

    @Transaction
    @Query("SELECT * FROM exposure_data WHERE state = :stateValue ORDER BY priority DESC")
    abstract suspend fun findBy(stateValue: Int): List<ExposureDataModel>

    @Transaction
    @Query("SELECT * FROM exposure_data WHERE planned_epoch < :baseTime AND state = :stateValue ORDER BY priority DESC")
    abstract suspend fun findTimeout(
        baseTime: Long,
        stateValue: Int = ExposureDataBaseModel.State.Started.value
    ): List<ExposureDataModel>

    open suspend fun upsert(
        exposureBaseData: ExposureDataModel,
    ): ExposureDataModel = upsert(
        exposureBaseData.exposureBaseData,
        exposureBaseData.diagnosisKeysFileList,
        exposureBaseData.exposureSummary,
        exposureBaseData.exposureInformationList,
        exposureBaseData.dailySummaryList,
        exposureBaseData.exposureWindowList,
    )

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    open suspend fun upsert(
        exposureBaseData: ExposureDataBaseModel,
        diagnosisKeysFileList: List<DiagnosisKeysFileModel> = listOf(),
        exposureSummary: ExposureSummaryModel? = null,
        exposureInformationList: MutableList<ExposureInformationModel> = mutableListOf(),
        dailySummaryList: MutableList<DailySummaryModel> = mutableListOf(),
        exposureWindowList: MutableList<ExposureWindowAndScanInstancesModel> = mutableListOf(),
    ): ExposureDataModel {
        val exposureDataId = upsert(exposureBaseData)

        diagnosisKeysFileList.forEach { model ->
            model.exposureDataId = exposureDataId
        }
        upsertDiagnosisKeysFileList(diagnosisKeysFileList)

        exposureSummary?.also { model ->
            model.exposureDataId = exposureDataId
            upsert(model)
        }
        exposureInformationList.also { modelList ->
            modelList.forEach { model -> model.exposureDataId = exposureDataId }
            upsert(modelList)
        }

        dailySummaryList.forEach { model ->
            model.exposureDataId = exposureDataId
        }
        upsertDailySummaryList(dailySummaryList)

        exposureWindowList.forEach { model ->
            model.exposureWindowModel.exposureDataId = exposureDataId
            upsert(model.exposureWindowModel, model.scanInstances)
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(exposureDataBaseModel: ExposureDataBaseModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertDiagnosisKeysFileList(
        diagnosisKeysFileList: List<DiagnosisKeysFileModel>
    ): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(dailySummaryModel: DailySummaryModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertDailySummaryList(
        dailySummaryList: List<DailySummaryModel>
    ): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(exposureWindowModel: ExposureWindowModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertScanInstances(
        scanInstanceModelList: List<ScanInstanceModel>
    ): List<Long>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    open suspend fun upsert(
        exposureWindowModel: ExposureWindowModel,
        scanInstanceModels: List<ScanInstanceModel>
    ): Long {
        val exposureWindowId = upsert(exposureWindowModel)

        scanInstanceModels.forEach { scanInstanceModel ->
            scanInstanceModel.exposureWindowId = exposureWindowId
        }

        upsertScanInstances(scanInstanceModels)

        return exposureWindowId
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(exposureSummaryModel: ExposureSummaryModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(exposureInformationList: List<ExposureInformationModel>): List<Long>

    @Transaction
    open suspend fun setTimeout(baseTime: Long, state: Int): List<ExposureDataModel> {
        val timeoutDataList = findTimeout(baseTime, state)
        timeoutDataList.forEach { data ->
            data.exposureBaseData.state = ExposureDataBaseModel.State.Timeout
            upsert(data)
        }
        return timeoutDataList
    }

    @Query("DELETE FROM exposure_data WHERE state = :stateValue")
    abstract fun cleanup(stateValue: Int)
}
