package dev.keiji.cocoa.android.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.ExposureDetectionService
import timber.log.Timber
import java.io.IOException

@HiltWorker
class DetectExposureWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val exposureDetectionService: ExposureDetectionService,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("Starting worker...")

        try {
            return exposureDetectionService.detectExposure()
        } catch (e: IOException) {
            Timber.e(e, e.javaClass.simpleName)
            return Result.retry()
        } catch (e: Exception) {
            Timber.e(e, e.javaClass.simpleName)
            return Result.failure()
        } finally {
            Timber.d("Starting finished.")
        }
    }
}
