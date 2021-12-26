package dev.keiji.cocoa.android.exposure_notificaiton.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.keiji.cocoa.android.exposure_notificaiton.entity.DiagnosisKeysFile

@Dao
interface DiagnosisKeysFileDao {

    @Query("SELECT * FROM diagnosiskeysfile")
    suspend fun getAll(): List<DiagnosisKeysFile>

    @Query("SELECT * FROM diagnosiskeysfile WHERE region = :region AND subregion = :subregion")
    suspend fun findAllByRegionAndSubregion(
        region: String,
        subregion: String?,
    ): List<DiagnosisKeysFile>

    @Query("SELECT * FROM diagnosiskeysfile WHERE region = :region AND subregion = :subregion AND is_processed = 1")
    suspend fun findAllByRegionAndSubregionNotProcessed(
        region: String,
        subregion: String?,
    ): List<DiagnosisKeysFile>

    @Insert
    suspend fun insertAll(diagnosisKeysFileList: List<DiagnosisKeysFile>)

    @Update
    suspend fun updateAll(diagnosisKeysFileList: List<DiagnosisKeysFile>)

    @Delete
    suspend fun delete(diagnosisKeysFile: DiagnosisKeysFile)

    @Delete
    suspend fun deleteAll(diagnosisKeysFileList: List<DiagnosisKeysFile>)
}