package dev.keiji.cocoa.android.exposure_notification.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.keiji.cocoa.android.exposure_notification.model.DailySummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureSummaryModel

@Dao
interface ExposureSummaryDao {

    @Query("SELECT * FROM exposure_summaries")
    suspend fun getAll(): List<ExposureSummaryModel>

    @Insert
    suspend fun insert(exposureInformation: ExposureSummaryModel): Long

    @Insert
    suspend fun insertAll(exposureInformationList: List<ExposureSummaryModel>): List<Long>
}
