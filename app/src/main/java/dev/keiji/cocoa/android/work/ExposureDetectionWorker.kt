package dev.keiji.cocoa.android.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.keiji.cocoa.android.ExposureNotificationWrapper
import dev.keiji.cocoa.android.api.DiagnosisKeyFileProvideServiceApi
import dev.keiji.cocoa.android.api.DiagnosisKeyListProvideServiceApi
import timber.log.Timber
import java.io.File
import java.io.IOException

@HiltWorker
class ExposureDetectionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    val diagnosisKeyListProvideServiceApi: DiagnosisKeyListProvideServiceApi,
    val diagnosisKeyFileProvideServiceApi: DiagnosisKeyFileProvideServiceApi,
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        private const val CLUSTER_ID = "345678"

        private const val DIR_NAME = "diagnosis_keys"
    }

    override suspend fun doWork(): Result {
        Timber.d("Starting worker...")

        val outputDir = getOutputDir()

        try {
            val diagnosisKeyList = diagnosisKeyListProvideServiceApi.getList(CLUSTER_ID)
            val downloadedFiles = diagnosisKeyList.map { diagnosisKeyEntry ->
                Timber.d(diagnosisKeyEntry.toString())
                diagnosisKeyEntry ?: return@map null

                return@map diagnosisKeyFileProvideServiceApi.getFile(diagnosisKeyEntry, outputDir)
            }
            return Result.success()
        } catch (e: IOException) {
            Timber.e(e.javaClass.simpleName, e)
            return Result.retry()
        } catch (e: Exception) {
            Timber.e(e.javaClass.simpleName, e)
            return Result.failure()
        } finally {
            Timber.d("Starting finished.")
        }
    }

    private fun getOutputDir(): File = File(File(applicationContext.filesDir, DIR_NAME), CLUSTER_ID)
}
