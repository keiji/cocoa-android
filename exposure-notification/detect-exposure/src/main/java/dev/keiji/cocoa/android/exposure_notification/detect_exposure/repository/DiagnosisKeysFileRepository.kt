package dev.keiji.cocoa.android.exposure_notification.detect_exposure.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.exposure_notification.dao.DiagnosisKeysFileDao
import dev.keiji.cocoa.android.exposure_notification.entity.DiagnosisKeysFile
import dev.keiji.cocoa.android.exposure_notification.source.DatabaseSource
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.api.DiagnosisKeyFileProvideServiceApi
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.api.DiagnosisKeyListProvideServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Singleton

interface DiagnosisKeysFileRepository {

    suspend fun getDiagnosisKeysFileList(
        region: String,
        subregion: String?
    ): List<DiagnosisKeysFile>

    suspend fun getDiagnosisKeysFile(diagnosisKeyFile: DiagnosisKeysFile): File?
    suspend fun setIsProcessed(diagnosisKeyFileList: List<DiagnosisKeysFile>)
}

class DiagnosisKeysFileRepositoryImpl(
    applicationContext: Context,
    private val pathSource: PathSource,
    private val dateTimeSource: DateTimeSource,
    private val diagnosisKeyFileDao: DiagnosisKeysFileDao,
    private val diagnosisKeyListProvideServiceApi: DiagnosisKeyListProvideServiceApi,
    private val diagnosisKeyFileProvideServiceApi: DiagnosisKeyFileProvideServiceApi,
) : DiagnosisKeysFileRepository {

    override suspend fun getDiagnosisKeysFileList(
        region: String,
        subregion: String?
    ): List<DiagnosisKeysFile> = withContext(Dispatchers.IO) {
        val diagnosisKeysFileEntryList = if (subregion != null) {
            diagnosisKeyListProvideServiceApi.getList(region, subregion)
        } else {
            diagnosisKeyListProvideServiceApi.getList(region)
        }

        val existKeyFileList = diagnosisKeyFileDao.findAllByRegionAndSubregion(region, subregion)

        val urlFileMap = HashMap<String, DiagnosisKeysFile>().also { map ->
            existKeyFileList.forEach { keyFile ->
                keyFile.isListed = false
                map[keyFile.url] = keyFile
            }
        }

        val newKeyFileList = mutableListOf<DiagnosisKeysFile>()

        diagnosisKeysFileEntryList
            .filterNotNull()
            .forEach { fileEntry ->
                if (urlFileMap.containsKey(fileEntry.url)) {
                    val keyFile = urlFileMap[fileEntry.url] ?: return@forEach
                    keyFile.isListed = true
                } else {
                    val keyFile = DiagnosisKeysFile(
                        id = 0,
                        region = region,
                        subregion = subregion,
                        url = fileEntry.url,
                        created = fileEntry.created,
                        isListed = true
                    )
                    newKeyFileList.add(keyFile)
                }
            }

        urlFileMap.clear()

        // insert
        diagnosisKeyFileDao.insertAll(newKeyFileList)

        // update
        diagnosisKeyFileDao.updateAll(existKeyFileList)

        // remove not-listed(expired) files
        val expiredFileList = existKeyFileList
            .filter { keyFile -> !keyFile.isListed }
        diagnosisKeyFileDao.deleteAll(expiredFileList)

        return@withContext diagnosisKeyFileDao.findAllByRegionAndSubregionNotProcessed(
            region,
            subregion
        )
    }

    override suspend fun getDiagnosisKeysFile(diagnosisKeyFile: DiagnosisKeysFile): File? =
        withContext(Dispatchers.IO) {
            val regionDir = File(pathSource.diagnosisKeysFileDir(), diagnosisKeyFile.region)

            val outputDir = if (diagnosisKeyFile.subregion != null) {
                File(regionDir, diagnosisKeyFile.subregion)
            } else {
                regionDir
            }

            try {
                return@withContext diagnosisKeyFileProvideServiceApi.downloadFile(
                    diagnosisKeyFile,
                    outputDir
                )
            } catch (e: IOException) {
                Timber.e("Download failed: ${diagnosisKeyFile.url}", e)
            } catch (e: Exception) {
                Timber.e("Download failed: ${diagnosisKeyFile.url}", e)
            }

            return@withContext null
        }

    override suspend fun setIsProcessed(diagnosisKeyFileList: List<DiagnosisKeysFile>) =
        withContext(Dispatchers.IO) {
            diagnosisKeyFileList.forEach { keyFile -> keyFile.isProcessed = true }

            diagnosisKeyFileDao.updateAll(diagnosisKeyFileList)
        }
}