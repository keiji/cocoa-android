package dev.keiji.cocoa.android.exposure_notification.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.keiji.cocoa.android.exposure_notification.model.ExposureInformationModel

@Dao
interface ExposureInformationDao {

    @Query("SELECT * FROM exposure_informations")
    suspend fun getAll(): List<ExposureInformationModel>

    @Insert
    suspend fun insert(exposureInformation: ExposureInformationModel): Long

    @Insert
    suspend fun insertAll(exposureInformationList: List<ExposureInformationModel>): List<Long>
}
