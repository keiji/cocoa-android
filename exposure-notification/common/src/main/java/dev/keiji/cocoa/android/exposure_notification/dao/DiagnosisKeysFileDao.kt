package dev.keiji.cocoa.android.exposure_notification.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.keiji.cocoa.android.exposure_notification.model.DiagnosisKeysFileModel

@Dao
abstract class DiagnosisKeysFileDao {

    @Query("SELECT * FROM diagnosis_keys_files")
    abstract suspend fun getAll(): List<DiagnosisKeysFileModel>

    @Query("SELECT * FROM diagnosis_keys_files WHERE region = :region")
    abstract suspend fun findAllBy(
        region: String
    ): List<DiagnosisKeysFileModel>

    open suspend fun findAllBy(
        region: String,
        subregion: String?,
    ): List<DiagnosisKeysFileModel> {
        return if (subregion != null) {
            findAllByRegion(region, subregion)
        } else {
            findAllByRegionAndSubregionNull(region)
        }
    }

    @Query("SELECT * FROM diagnosis_keys_files WHERE region = :region AND subregion = :subregion")
    abstract suspend fun findAllByRegion(
        region: String,
        subregion: String
    ): List<DiagnosisKeysFileModel>

    @Query("SELECT * FROM diagnosis_keys_files WHERE region = :region AND subregion is null")
    abstract suspend fun findAllByRegionAndSubregionNull(
        region: String,
    ): List<DiagnosisKeysFileModel>

    open suspend fun findNotCompleted(
        region: String,
    ): List<DiagnosisKeysFileModel> {
        return findAllByLessThanState(region, DiagnosisKeysFileModel.State.Completed.value)
    }

    open suspend fun findNotCompleted(
        region: String,
        subregion: String?,
    ): List<DiagnosisKeysFileModel> {
        return findAllByLessThanState(
            region,
            subregion,
            DiagnosisKeysFileModel.State.Completed.value
        )
    }

    @Query("SELECT * FROM diagnosis_keys_files WHERE region = :region AND state < :stateValue")
    abstract suspend fun findAllByLessThanState(
        region: String,
        stateValue: Int,
    ): List<DiagnosisKeysFileModel>

    @Query("SELECT * FROM diagnosis_keys_files WHERE region = :region AND subregion = :subregion AND state < :stateValue")
    abstract suspend fun findAllByLessThanState(
        region: String,
        subregion: String?,
        stateValue: Int,
    ): List<DiagnosisKeysFileModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun upsert(diagnosisKeysFileModel: DiagnosisKeysFileModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun upsert(diagnosisKeysFileModelList: List<DiagnosisKeysFileModel>): List<Long>

    @Insert
    abstract suspend fun insertAll(diagnosisKeysFileModelList: List<DiagnosisKeysFileModel>): List<Long>

    @Update
    abstract suspend fun updateAll(diagnosisKeysFileModelList: List<DiagnosisKeysFileModel>)

    @Delete
    abstract suspend fun delete(diagnosisKeysFileModel: DiagnosisKeysFileModel)

    @Delete
    abstract suspend fun deleteAll(diagnosisKeysFileModelList: List<DiagnosisKeysFileModel>)
}
