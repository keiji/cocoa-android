package dev.keiji.cocoa.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.ExposureDetectionService
import dev.keiji.cocoa.android.work.V1ExposureDetectionWorker
import dev.keiji.cocoa.android.work.V2ExposureDetectionWorker
import dev.keiji.cocoa.android.work.NoExposureDetectionWorker
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class ExposureDetectionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var dateTimeSource: DateTimeSource

    @Inject
    lateinit var exposureNotificationService: ExposureDetectionService

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.i("ExposureDetectionReceiver.onReceive")

        context ?: return
        intent ?: return
        val intentAction = intent.action ?: return

        val async = goAsync()

        try {
            runBlocking {
                exposureNotificationService.onResultReceived(intentAction)
            }

            val workManager = WorkManager.getInstance(context)

            when (intentAction) {
                ExposureNotificationWrapper.ACTION_EXPOSURE_NOT_FOUND -> {
                    onNotDetectExposure(workManager)
                }
                ExposureNotificationWrapper.ACTION_EXPOSURE_STATE_UPDATED -> {
                    onDetectExposure(workManager, intent)
                }
            }
        } finally {
            async.finish()
        }
    }

    private fun onNotDetectExposure(workManager: WorkManager) {
        Timber.i("No exposure detected.")

        NoExposureDetectionWorker.enqueue(
            workManager,
        )
    }

    private fun onDetectExposure(workManager: WorkManager, intent: Intent) {
        Timber.i("Exposure detected.")

        val isV1Api = intent.hasExtra(ExposureNotificationWrapper.EXTRA_EXPOSURE_SUMMARY)

        if (isV1Api) {
            val token = intent.getStringExtra(ExposureNotificationWrapper.EXTRA_TOKEN) ?: return
            V1ExposureDetectionWorker.enqueue(
                workManager,
                token
            )
        } else {
            V2ExposureDetectionWorker.enqueue(
                workManager,
            )
        }
    }
}
