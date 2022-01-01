package dev.keiji.cocoa.android.exposure_notification.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.keiji.cocoa.android.exposure_notification.model.DiagnosisKeysFileModel

@Dao
interface DiagnosisKeysFileDao {

    @Query("SELECT * FROM diagnosiskeysfile")
    suspend fun getAll(): List<DiagnosisKeysFileModel>

    @Query("SELECT * FROM diagnosiskeysfile WHERE region = :region AND subregion = :subregion")
    suspend fun findAllByRegionAndSubregion(
        region: String,
        subregion: String?,
    ): List<DiagnosisKeysFileModel>

    @Query("SELECT * FROM diagnosiskeysfile WHERE region = :region AND subregion = :subregion AND is_processed = 1")
    suspend fun findAllByRegionAndSubregionNotProcessed(
        region: String,
        subregion: String?,
    ): List<DiagnosisKeysFileModel>

    @Insert
    suspend fun insertAll(diagnosisKeysFileModelList: List<DiagnosisKeysFileModel>)

    @Update
    suspend fun updateAll(diagnosisKeysFileModelList: List<DiagnosisKeysFileModel>)

    @Delete
    suspend fun delete(diagnosisKeysFileModel: DiagnosisKeysFileModel)

    @Delete
    suspend fun deleteAll(diagnosisKeysFileModelList: List<DiagnosisKeysFileModel>)
}
