package dev.keiji.cocoa.android.exposure_notification.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.keiji.cocoa.android.exposure_notification.model.DailySummaryModel

@Dao
interface DailySummaryDao {

    @Query("SELECT * FROM daily_summaries")
    suspend fun getAll(): List<DailySummaryModel>

    @Insert
    suspend fun insert(dailySummary: DailySummaryModel): Long

    @Insert
    suspend fun insertAll(dailySummaryList: List<DailySummaryModel>): List<Long>
}
