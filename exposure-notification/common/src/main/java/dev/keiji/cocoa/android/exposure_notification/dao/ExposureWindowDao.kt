package dev.keiji.cocoa.android.exposure_notification.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowAndScanInstancesModel
import dev.keiji.cocoa.android.exposure_notification.model.ScanInstanceModel

@Dao
abstract class ExposureWindowDao {

    @Transaction
    @Query("SELECT * FROM exposure_windows")
    abstract suspend fun getAll(): List<ExposureWindowAndScanInstancesModel>

    @Query("SELECT * FROM exposure_windows WHERE date_millis_since_epoch > :fromDateMillisSinceEpoch")
    abstract fun findBy(fromDateMillisSinceEpoch: Long): List<ExposureWindowAndScanInstancesModel>

    @Insert
    abstract suspend fun insert(exposureWindowModel: ExposureWindowModel): Long

    @Insert
    abstract suspend fun insertAll(exposureWindowModelList: List<ExposureWindowModel>): List<Long>

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
    abstract suspend fun insertScanInstances(scanInstanceModelList: List<ScanInstanceModel>): List<Long>

    @Transaction
    @Query("SELECT * FROM scan_instances WHERE exposure_window_id = :exposureWindowId")
    abstract suspend fun findScanInstancesByExposureWindowId(exposureWindowId: Long): List<ScanInstanceModel>
}
